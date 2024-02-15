package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.dto.RequestBlogDto;
import camel.BoostMarketer.blog.dto.keywordDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.api.Crawler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogMapper blogMapper;

    public void create(RequestBlogDto requestBlogDto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String postNo = requestBlogDto.getPostNo();
        String blogId = requestBlogDto.getBlogId();

        String paramUrl = "blogId=" + blogId + "&logNo=" + postNo;

        int visitorCnt = Crawler.visitorCntCrawler(blogId); //방문자 수 크롤링


        Map<String, Object> postInfo = Crawler.postInfoCrawler(paramUrl, postNo); //블로그 글정보
        postInfo.put("blogId", blogId);
        postInfo.put("postNo", postNo);
        BlogPostDto blogPostDto = mapper.convertValue(postInfo, BlogPostDto.class);

        blogMapper.insertBlogPost(blogPostDto);


        List<Integer> rankList = Crawler.rankCrawler(requestBlogDto); //키워드 랭킹

        keywordDto keywordDto;
//        List<keywordDto> keyWordDtoList = new ArrayList<>();

        for(int i=0; i<rankList.size(); i++){
            keywordDto = new keywordDto(requestBlogDto.getPostNo(),requestBlogDto.getKeyWord().get(i), rankList.get(i));
            blogMapper.insertKeyword(keywordDto);
            blogMapper.insertKeywordRank(keywordDto);
//            keyWordDtoList.add(keywordDto);
        }
    }

    public List<BlogPostDto> selectPostList() throws Exception {
        return blogMapper.selectPostList();
    }

    public List<keywordDto> selectKeyWordRank(List<BlogPostDto> blogPostDtoList) throws Exception {
        return blogMapper.selectKeyWordRank(blogPostDtoList);
    }
}
