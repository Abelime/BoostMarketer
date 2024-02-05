package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.api.ApiSearchBlog;
import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.repository.MemoryBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final MemoryBlogRepository blogRepository;

    private final ApiSearchBlog apiSearchBlog;
    public void create(BlogDto blogDto) {
        blogRepository.save(blogDto);
        apiSearchBlog.apiAccess(blogDto);

    }


}
