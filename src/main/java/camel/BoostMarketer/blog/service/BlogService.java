package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.api.CrawlerRank;
import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.repository.MemoryBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final MemoryBlogRepository blogRepository;

    private final CrawlerRank crawlerRank;

    public void create(BlogDto blogDto) {
        blogRepository.save(blogDto);
//        apiSearchBlog.apiAccess(blogDto);
        crawlerRank.getRank(blogDto);

    }


}
