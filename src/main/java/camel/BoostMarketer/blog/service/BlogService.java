package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.api.Crawler;
import camel.BoostMarketer.blog.dto.*;
import camel.BoostMarketer.blog.repository.MemoryBlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final MemoryBlogRepository blogRepository;

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


        List<Integer> rankList = Crawler.rankCrawler(requestBlogDto); //키워드 랭킹
        KeyWordDto keyWordDto = new KeyWordDto(requestBlogDto.getKeyWord(), rankList);

        System.out.println("성공 자자");



    }


}
