package camel.BoostMarketer.keyword.service;

import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.ConvertBlogUrl;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import camel.BoostMarketer.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class KeywordBatchService {

    private final KeywordMapper keywordMapper;

    private final BlogMapper blogMapper;

    private final UserMapper userMapper;

    public void updateKeywordRank() throws Exception {
        //모든 userID 조회
        List<Long> userIdList = userMapper.findByIds();

        for (Long userId : userIdList) {
            List<String> blogIdList = blogMapper.findBlogIdByUserId(userId);
            List<KeywordDto> keywordList = keywordMapper.findKeywordNameByUserId(userId);

            for (KeywordDto keywordDto : keywordList) {
                NaverSearchAdApi.apiAccess(keywordDto);
                keywordMapper.updateKeywordDict(keywordDto);

                // 랜덤 타이머 설정
                sleepRandomly();

                //통합검색 노출(크롤링)
                List<String> totalSearchResult = Crawler.totalSearchCrawler(blogIdList, keywordDto.getKeywordName());

                //블로그 탭 랭킹(크롤링)
                // blogUrl, rank
                Map<String, Integer> blogTabResult = Crawler.blogTabCrawler(blogIdList, keywordDto.getKeywordName());


                List<KeywordDto> keywordDtoList = new ArrayList<>();

                for (String blogUrl : blogTabResult.keySet()) {
                    KeywordDto rankDto = new KeywordDto();
                    rankDto.setKeywordId(keywordDto.getKeywordId());
                    rankDto.setRankPc(blogTabResult.get(blogUrl));
                    rankDto.setPostNo(ConvertBlogUrl.urlToPostNo(blogUrl));
                    if (!totalSearchResult.isEmpty() && totalSearchResult.contains(blogUrl)) {
                        rankDto.setTotalSearchExposure(1);
                        totalSearchResult.remove(blogUrl);
                    }
                    keywordDtoList.add(rankDto);
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
                    for (KeywordDto dto : keywordDtoList) {
                        keywordMapper.updateKeywordRank(dto);
                    }
                }
            }
        }

        //순위X,통검 노출X 데이터 삭제
//        keywordMapper.deleteRankData();

    }

    private void sleepRandomly() throws InterruptedException {
        Random random = new Random();
        // 0초에서 3초 사이의 랜덤 대기 시간 생성
        int sleepTime = 1500 + random.nextInt(1500);
        Thread.sleep(sleepTime);
    }

}
