package camel.BoostMarketer.blog.controller;

import camel.BoostMarketer.blog.dto.BlogDto;
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
    public ResponseEntity<?> createBlog(BlogDto blogDto) throws Exception {
        blogService.create(blogDto);
        logger.trace("Trace Level 테스트");
        logger.debug("DEBUG Level 테스트");
        logger.info("INFO Level 테스트");
        logger.warn("Warn Level 테스트");
        logger.error("ERROR Level 테스트");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
