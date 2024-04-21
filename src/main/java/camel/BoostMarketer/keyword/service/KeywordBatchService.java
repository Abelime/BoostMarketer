package camel.BoostMarketer.keyword.service;

import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import camel.BoostMarketer.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordBatchService {

    private final KeywordMapper keywordMapper;

    private final BlogMapper blogMapper;

    private final KeywordService keywordService;

    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void updateKeywordDict() throws Exception {
        List<KeywordDto> allKeyword = keywordMapper.findAllKeyword();

        for (KeywordDto keywordDto : allKeyword) {
            NaverSearchAdApi.apiAccess(keywordDto);
            keywordMapper.updateKeywordDict(keywordDto);
        }

    }

    public void updateKeywordRank() throws Exception {
        //모든 userID 조회
        List<Long> userIdList = userMapper.findByIds();

        int attempts = 0;

        for (Long userId : userIdList) {
            List<String> blogIdList = blogMapper.findBlogIdByUserId(userId);
            List<KeywordDto> keywordList = keywordMapper.findKeywordNameByUserId(userId);

            attempts = keywordService.cralwerProcess(keywordList, blogIdList, attempts);
        }

        Map<String, String> headerData = Crawler.headerData;
        headerData.clear();

        //블로그탭 순위X, 통검 노출X = 데이터 삭제
        keywordMapper.deleteRankData();
    }

}
