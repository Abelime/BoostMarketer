package camel.BoostMarketer.keyword.service;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.ExcelUtil;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordMapper keywordMapper;

    private final BlogMapper blogMapper;

    public List<KeywordDto> selectKeywordInfo() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return keywordMapper.selectKeywordInfo(email);
    }

    public void registerKeyword(KeywordDto keywordDto) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        //검색량 조회
        NaverSearchAdApi.apiAccess(keywordDto);

        keywordMapper.registerKeywordDict(keywordDto);

        keywordMapper.registerUserKeyword(keywordDto, email);

        List<BlogDto> blogDtoList = blogMapper.selectBlogInfo(email);

        List<String> blogIdList = blogDtoList.stream()
                .map(BlogDto::getBlogId)
                .toList();

        List<KeywordDto> keywordDtoList = Crawler.newRankCrawler(blogIdList, keywordDto.getKeywordName(), keywordDto.getKeywordId());

        if(!keywordDtoList.isEmpty()){
            keywordMapper.registerKeywordRank(keywordDtoList);
        }



    }

    public void keywordExcelUpload(MultipartFile file) throws Exception {
        List<HashMap<String, Object>> list = new ArrayList<>();
        KeywordDto keywordDto;

        ExcelUtil util = new ExcelUtil();
        util.excelUpload(file, list);

        for (HashMap<String, Object> map : list) {
            keywordDto = new KeywordDto();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                keywordDto.setCategory(entry.getKey().replaceAll("[^0-9]", ""));
                keywordDto.setKeywordName(entry.getValue().toString());
                registerKeyword(keywordDto);
            }
        }

    }

}
