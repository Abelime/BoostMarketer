package camel.BoostMarketer.blog.controller;

import camel.BoostMarketer.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequiredArgsConstructor
public class BlogController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    @GetMapping(value = "/blog")
    public String blogForm(Model model,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                           @RequestParam(value = "sort", defaultValue = "general") String sort) throws Exception {
        Map<String, Object> resultMap = blogService.selectBlogInfo(page, pageSize, sort);


        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sort", sort);
        model.addAttribute("lastUpdateDt", resultMap.get("lastUpdateDt"));
        model.addAttribute("totalCount", resultMap.get("totalBlogCount"));
        model.addAttribute("totalPostCnt", resultMap.get("totalPostCnt"));
        model.addAttribute("blogList", resultMap.get("blogDtoList"));
        return "pages/blog";
    }

    @PostMapping(value = "/blog/{blogId}")
    public ResponseEntity<?> registerBlogUrl(@PathVariable("blogId") String blogId) throws Exception {
        blogService.registerUrl(blogId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/blog/{blogId}")
    public ResponseEntity<?> updateBlog(@PathVariable("blogId") String blogId) throws Exception {
        blogService.updateBlog(blogId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/blog/{blogId}")
    public ResponseEntity<?> deleteBlog(@PathVariable("blogId") String blogId) throws Exception {
        blogService.deleteBlog(blogId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/blog/popup/{blogId}")
    public String blogPopUp(Model model,@PathVariable("blogId") String blogId) throws Exception {
        Map<String, Object> resultMap = blogService.getRankedKeywordsByBlog(blogId);

        model.addAttribute("blogInfoDto", resultMap.get("blogInfoDto"));
        model.addAttribute("postInfoList", resultMap.get("postInfoList"));
        model.addAttribute("keywordRankInfo", resultMap.get("keywordRankInfo"));
        return "common/blog-popup";
    }
    @GetMapping(value = "/blog/popup/missing/{blogId}")
    public String blogMissingPopUp(Model model,@PathVariable("blogId") String blogId) throws Exception {
        Map<String, Object> resultMap = blogService.getMissingPostByBlog(blogId);

        model.addAttribute("blogInfoDto", resultMap.get("blogInfoDto"));
        model.addAttribute("blogPostDtos", resultMap.get("blogPostDtos"));
        return "common/blog-missing-popup";
    }

}