package camel.BoostMarketer.experience.service;

import camel.BoostMarketer.common.ConvertBlogUrl;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverAdApi;
import camel.BoostMarketer.common.api.NaverBlogApi;
import camel.BoostMarketer.experience.dto.ExperienceDto;
import camel.BoostMarketer.experience.dto.ExperienceKeywordDto;
import camel.BoostMarketer.experience.dto.ExperienceLinkDto;
import camel.BoostMarketer.experience.dto.ExperienceResponseDto;
import camel.BoostMarketer.experience.mapper.ExperienceMapper;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import camel.BoostMarketer.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExperienceMapper experienceMapper;

    private final KeywordMapper keywordMapper;

    private final NaverAdApi naverAdApi;

    private final NaverBlogApi naverBlogApi;

    private final KeywordService keywordService;

    @Qualifier("customTaskExecutor")
    private final TaskExecutor taskExecutor;

    public Map<String, Object> selectExperienceInfo(int page, int pageSize, String searchKeyword) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int offset = (page - 1) * pageSize;
        RowBounds rowBounds = new RowBounds(offset, pageSize);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        int totalCount = experienceMapper.selectExperienceCntInfo(email, searchKeyword);
        List<ExperienceResponseDto> experienceResponseDtoList =  experienceMapper.selectExperienceInfo(email, rowBounds, searchKeyword);

        for (ExperienceResponseDto exp : experienceResponseDtoList) {
            if (exp.getThumbnail() != null) {
                String base64Image = Base64.getEncoder().encodeToString(exp.getThumbnail());
                exp.setThumbnailBase64("data:image/jpeg;base64," + base64Image);
            }
        }

        resultMap.put("experienceResponseDtoList", experienceResponseDtoList);
        resultMap.put("totalCount", totalCount);


        return resultMap;
    }

    public void saveExperience(ExperienceDto experienceDto) throws Exception{
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ExperienceLinkDto> experienceLinkDtoList = new ArrayList<>();
        List<ExperienceKeywordDto> experienceKeywordDtoList = new ArrayList<>();
        List<KeywordDto> keywordDtoList = new ArrayList<>();

        experienceMapper.save(experienceDto, email); //체험단 등록
        Long experienceId = experienceDto.getId();

        for (String keyword : experienceDto.getKeywords()) { //키워드 등록
            KeywordDto keywordDto = new KeywordDto();
            keywordDto.setKeywordName(keyword);

            naverAdApi.apiAccess(keywordDto); //검색량 조회
            keywordMapper.registerKeywordDict(keywordDto);//키워드 사전 등록

            Long keywordId = keywordDto.getKeywordId();

            ExperienceKeywordDto experienceKeywordDto = new ExperienceKeywordDto();
            experienceKeywordDto.setKeywordId(keywordId);
            experienceKeywordDto.setExperienceId(experienceId);
            experienceKeywordDtoList.add(experienceKeywordDto);
            keywordDtoList.add(keywordDto);
        }
        experienceMapper.saveKeyword(experienceKeywordDtoList);

        for (String link : experienceDto.getLinks()) {
            ExperienceLinkDto experienceLinkDto;
            if (link.contains("blog.naver.com")) {
                Map<String, String> urlMap = ConvertBlogUrl.convertUrl(link);
                experienceLinkDto = naverBlogApi.blogPostInfoApi(urlMap.get("blogId"), urlMap.get("postNo"));
            } else {
                experienceLinkDto = new ExperienceLinkDto();
            }
            experienceLinkDto.setExperienceId(experienceId);
            experienceLinkDto.setLink(link);
            experienceLinkDtoList.add(experienceLinkDto);
        }

        experienceMapper.saveLink(experienceLinkDtoList);

        taskExecutor.execute(() -> {
            try {
                keywordService.cralwerProcess(keywordDtoList, experienceDto.getLinks(), 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                Crawler.headerData.clear();
            }
        });

    }


    public ExperienceResponseDto getExperienceDetail(Long id) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ExperienceResponseDto experienceResponseDto = experienceMapper.selectExperienceById(id, email);
        if (experienceResponseDto.getThumbnail() != null) {
            String base64Image = Base64.getEncoder().encodeToString(experienceResponseDto.getThumbnail());
            experienceResponseDto.setThumbnailBase64("data:image/jpeg;base64," + base64Image);
        }
        List<String> experienceKeywordList = experienceMapper.selectExperienceKeywordById(id);
        experienceResponseDto.setKeyword(experienceKeywordList );

        return experienceResponseDto;
    }
}
