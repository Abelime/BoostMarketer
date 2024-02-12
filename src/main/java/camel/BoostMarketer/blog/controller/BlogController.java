package camel.BoostMarketer.blog.controller;

import camel.BoostMarketer.blog.dto.*;
import camel.BoostMarketer.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class BlogController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    @PostMapping(value = "/blog/new")
    public ResponseEntity<?> createBlog(RequestBlogDto requestBlogDto) throws Exception {
        logger.debug(requestBlogDto.toString());
        blogService.create(requestBlogDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
