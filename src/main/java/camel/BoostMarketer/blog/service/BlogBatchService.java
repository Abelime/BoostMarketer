package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.api.Crawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlogBatchService {

    private final BlogMapper blogMapper;

    public void updateBlogPost() throws Exception {
        List<BlogPostDto> blogDtoList = blogMapper.selectAllLastPostNo();

        List<String> blogIdList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        for (BlogPostDto blogPostDto : blogDtoList) {
            blogIdList.add(blogPostDto.getBlogId());
            map.put(blogPostDto.getBlogId(), blogPostDto.getPostNo());
        }

        List<BlogPostDto> blogPostDtoList = Crawler.allPostCrawler(blogIdList, map);

        if (!blogPostDtoList.isEmpty()) {
            blogMapper.registerPosts(blogPostDtoList);
        }

        blogMapper.allBlogUpdatedAt("");

    }

}
