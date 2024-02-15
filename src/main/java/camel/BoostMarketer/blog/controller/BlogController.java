package camel.BoostMarketer.blog.controller;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.dto.RequestBlogDto;
import camel.BoostMarketer.blog.dto.keywordDto;
import camel.BoostMarketer.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class BlogController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    @GetMapping(value = "/")
    public String home() throws Exception {
        return "home";
    }

    @GetMapping(value = "/blog/new")
    public String createForm() throws Exception {
        return "/blog/createBlogForm";
    }

    @PostMapping(value = "/blog/new")
    public ResponseEntity<?>  createBlog(Model model, RequestBlogDto requestBlogDto) throws Exception {
        blogService.create(requestBlogDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/blog/list")
    public String blogList(Model model) throws Exception {
        List<BlogPostDto> blogPostDtoList = blogService.selectPostList();
        List<keywordDto> keywordDtoList = blogService.selectKeyWordRank(blogPostDtoList);

        model.addAttribute("postList", blogPostDtoList);
        model.addAttribute("keywordList", keywordDtoList);

        return "/blog/blogList";
    }



}
