package camel.BoostMarketer.keyword.service;

import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.ConvertBlogUrl;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverAdApi;
import camel.BoostMarketer.common.util.ExcelUtil;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import camel.BoostMarketer.user.dto.UserDto;
import camel.BoostMarketer.user.mapper.UserMapper;
import generator.RandomUserAgentGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@EnableAsync
@Service
public class KeywordService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final KeywordMapper keywordMapper;

    private final BlogMapper blogMapper;

    private final UserMapper userMapper;

    private final NaverAdApi naverAdApi;


    @Qualifier("customTaskExecutor")
    private final TaskExecutor taskExecutor;

    public Map<String, Object> selectKeywordsInfo(int page, int pageSize, int filterCategory, String sort, String searchKeyword) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int offset = (page - 1) * pageSize;
        RowBounds rowBounds = new RowBounds(offset, pageSize);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, Object> keywordCntInfo = keywordMapper.selectKeywordCntInfo(email, filterCategory, searchKeyword);
        List<KeywordDto> keywordDtoList = keywordMapper.selectKeywordInfo(email, filterCategory, sort, rowBounds, searchKeyword);
        String completeDate = keywordMapper.selectCompleteDate();

        resultMap.put("completeDate", completeDate);
        resultMap.put("keywordRankCount", keywordCntInfo.get("keywordRankCount"));
        resultMap.put("keywordCount", keywordCntInfo.get("keywordCount"));
        resultMap.put("keywordCntInfo", keywordCntInfo);
        resultMap.put("keywordDtoList", keywordDtoList);
        return resultMap;
    }

    public void registerKeyword(KeywordDto keywordDto) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        int attempts = 0;

        keywordRegister(keywordDto, email); //키워드 등록

        List<String> blogIdList = blogMapper.selectBlogIdList(email);//등록한 blogId 조회

        taskExecutor.execute(() -> {
            try {
                cralwerProcess(Collections.singletonList(keywordDto), blogIdList, attempts);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Crawler.headerData.clear();

    }

    public void keywordExcelUpload(MultipartFile file) throws Exception {
        List<HashMap<String, Object>> excelData = new ArrayList<>();
        ExcelUtil.excelUpload(file, excelData);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<KeywordDto> keywordDtoList = new ArrayList<>();

        KeywordDto keywordDto;

        for (HashMap<String, Object> map : excelData) {
            keywordDto = new KeywordDto();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                keywordDto.setCategoryId(Integer.parseInt(entry.getKey()));
                if (!entry.getValue().toString().isEmpty()) {
                    keywordDto.setKeywordName(entry.getValue().toString());
                    keywordRegister(keywordDto, email); //키워드 등록
                    keywordDtoList.add(keywordDto);
                }
            }
        }

        taskExecutor.execute(() -> {
            try {
                keywordExcelBackgroundTask(keywordDtoList, email);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Crawler.headerData.clear();

    }

    private void keywordRegister(KeywordDto keywordDto, String email) throws Exception {
        //검색량 조회
        naverAdApi.apiAccess(keywordDto);
        //키워드 등록(사전)
        keywordMapper.registerKeywordDict(keywordDto);
        //키워드 등록(유저)
        keywordMapper.registerUserKeyword(keywordDto, email);
    }

    @Async
    public void keywordExcelBackgroundTask(List<KeywordDto> keywordDtoList, String email) throws Exception {
        List<String> blogIdList = blogMapper.selectBlogIdList(email);
        int attempts = 0; //재시도 횟수

        cralwerProcess(keywordDtoList, blogIdList, attempts);
        Map<String, String> headerData = Crawler.headerData;
        headerData.clear();
    }

    public void keywordDelete(List<Long> keywordIds) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userMapper.findByEmail(email);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id",userDto.getId().toString());
        map.put("keywordIds", keywordIds);

        keywordMapper.deleteKeyDict(map);
        keywordMapper.deleteUserKey(map);
        keywordMapper.deleteKeyRank1(map);
        keywordMapper.deleteKeyRank2(map);
    }

    public HashMap<String, Object> selectKeywordCntInfo(String email) throws Exception {
        return keywordMapper.selectKeywordCntInfo(email, 0, "");
    }

    public void keywordFix(Long keywordId) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        keywordMapper.keywordFix(email,keywordId);
    }

    public List<KeywordDto> selectKeywordInfo(String postNo) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return keywordMapper.selectRankKeywordByPostNo(postNo, email);
    }

    public void changeCategory(int category, List<String> keywordIdList) throws Exception {
        keywordMapper.updateKeywordCategory(category, keywordIdList);
    }

    public void updateCategory(List<Integer> categoryIdList, List<String> categoryNameList) throws Exception {
        Map<String, Object> param = new HashMap<>();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        param.put("email", email);

        for(int i=0; i<categoryIdList.size(); i++){
            param.put("categoryId", categoryIdList.get(i));
            param.put("categoryName", categoryNameList.get(i));
            keywordMapper.updateCategory(param);
        }
    }

    public List<KeywordDto> selectCategoryInfo() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return keywordMapper.selectCategory(email);
    }


    public int cralwerProcess(List<KeywordDto> keywordList, List<String> blogIdList, int attempts) {
        for (KeywordDto keywordDto : keywordList) {
            boolean success = false;

            while (!success && attempts < 4) {
                try {
                    if (Crawler.headerData.get("User-Agent") != null) {
                        Crawler.headerData.put("User-Agent", RandomUserAgentGenerator.getNextNonMobile());
                    }
                    cralwerStart(blogIdList, keywordDto);
                    success = true;
                } catch (Exception e) {
                    if (e.getMessage().contains("403")) {
                        logger.error("크롤링 재시도 횟수: " + (attempts + 1));
                        Crawler.updateHeaderData(attempts);
                        attempts++;
                        String reason = "Exception";
                        Crawler.sleep(reason);
                    } else {
                        logger.error("크롤링 실패 [키워드]: " + keywordDto.getKeywordName(), e);
                        break;
                    }
                }
            }

            if (!success) {
                logger.error("크롤링 실패 [키워드]: " + keywordDto.getKeywordName() + ", 최대 재시도 횟수 초과");
            }
        }
        return attempts;
    }

    private void cralwerStart(List<String> blogIdList, KeywordDto keywordDto) throws Exception {
        String keyword = keywordDto.getKeywordName();

        //통합검색 크롤링
        List<String> totalSearchResult = Crawler.totalSearchCrawler(blogIdList, keyword);
        Crawler.sleep("random");

        //블로그탭 크롤링
        Map<String, Integer> blogTabResult = Crawler.blogTabCrawler(blogIdList, keyword);
        Crawler.sleep("random");

        //데이터 가공 및 저장
        if (!totalSearchResult.isEmpty() || !blogTabResult.isEmpty()) {
            insertUniqueCrawlerData(keywordDto, blogTabResult, totalSearchResult);
        }
    }

    private void insertUniqueCrawlerData(KeywordDto keywordDto, Map<String, Integer> blogTabResult, List<String> totalSearchResult) throws Exception {
        List<KeywordDto> keywordDtoList = new ArrayList<>();

        for (String blogUrl : blogTabResult.keySet()) {
            KeywordDto dto = new KeywordDto();
            dto.setKeywordId(keywordDto.getKeywordId());
            dto.setRankPc(blogTabResult.get(blogUrl));
            dto.setPostNo(ConvertBlogUrl.urlToPostNo(blogUrl));
            if (!totalSearchResult.isEmpty() && totalSearchResult.contains(blogUrl)) {
                dto.setTotalSearchExposure(1);
                totalSearchResult.remove(blogUrl);
            }
            keywordDtoList.add(dto);
        }

        if (!totalSearchResult.isEmpty()) {
            for (String blogUrl : totalSearchResult) {
                KeywordDto dto = new KeywordDto();
                dto.setKeywordId(keywordDto.getKeywordId());
                dto.setPostNo(ConvertBlogUrl.urlToPostNo(blogUrl));
                dto.setTotalSearchExposure(1);
                keywordDtoList.add(dto);
            }
        }

        if (!keywordDtoList.isEmpty()) {
            keywordMapper.upsertKeywordRank(keywordDtoList);
        }
    }
}
