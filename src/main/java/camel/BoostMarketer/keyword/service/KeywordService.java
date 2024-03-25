package camel.BoostMarketer.keyword.service;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.ExcelUtil;
import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordMapper keywordMapper;

    private final BlogMapper blogMapper;

    public void registerKeyword(KeywordDto keywordDto) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        

        NaverSearchAdApi.apiAccess(keywordDto);

        Long keywordId = keywordMapper.registerKeywordDict(keywordDto);
        keywordDto.setKeywordId(keywordId);

        keywordMapper.registerUserKeyword(keywordDto, email);

        List<BlogDto> blogDtos = blogMapper.selectBlogInfo(email);

        Map<String, Object> map = new HashMap<>();

        map.put("blogId", blogDtos);
        map.put("keyword", keywordDto.getKeywordName());
//        Crawler.newRankCrawler(map);



    }

    public void keywordExcelUpload(MultipartFile file) throws Exception {
        ExcelUtil util = new ExcelUtil();
        util.excelUpload(file);
    }

}
