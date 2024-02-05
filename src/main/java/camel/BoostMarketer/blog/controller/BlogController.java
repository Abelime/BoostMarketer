package camel.BoostMarketer.blog.controller;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping(value = "/blog/new")
    public ResponseEntity<?> createBlog(BlogDto blogDto) throws Exception {
        blogService.create(blogDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
