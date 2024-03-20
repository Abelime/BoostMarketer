package camel.BoostMarketer.blog.controller;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.dto.KeywordDto;
import camel.BoostMarketer.blog.dto.RequestBlogDto;
import camel.BoostMarketer.blog.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequiredArgsConstructor
public class BlogController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    @GetMapping(value = "/blog")
    public String blogForm(Model model) throws Exception {
        List<BlogDto> blogDtoList = blogService.selectBlogInfo();

        int totalPostCnt = 0;

        for (BlogDto blogDto : blogDtoList) {
            totalPostCnt += blogDto.getPostCnt();
        }

        model.addAttribute("totalPostCnt", totalPostCnt);
        model.addAttribute("blogList", blogDtoList);
        return "pages/blog";
    }

    @PostMapping(value = "/blog")
    public ResponseEntity<?> registerBlogUrl(@RequestBody List<String> blogId) throws Exception {
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


    @GetMapping(value = "/blog/new")
    public String createForm() throws Exception {
        return "blog/createBlogForm";
    }

    @PostMapping(value = "/blog/new")
    public ResponseEntity<?>  createBlog(Model model, RequestBlogDto requestBlogDto) throws Exception {
        blogService.create(requestBlogDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/blog/list")
    public String blogList(Model model) throws Exception {
        List<BlogPostDto> blogPostDtoList = blogService.selectPostList();
        List<KeywordDto> keywordDtoList = blogService.selectKeyWordRank(blogPostDtoList);

        model.addAttribute("postList", blogPostDtoList);
        model.addAttribute("keywordList", keywordDtoList);

        return "blog/blogList";
    }

    @GetMapping(value = "/blog/detail/{postNo}")
    public String detailBlogPost(Model model, @PathVariable("postNo") String postNo) throws Exception {
        BlogPostDto post = blogService.selectDetailPost(postNo);

        List<Map<String, Object>> keywordList = blogService.selectDetailRank(postNo);
        Set<String> uniqueDatesSet = new HashSet<>();

        for (Map<String, Object> keyword : keywordList) {
            // rankDates 추출을 위해 각 keyword 맵에서 "rankDates" 키의 값을 가져옵니다.
            List<Map<String, String>> rankDates = (List<Map<String, String>>) keyword.get("rankDates");
            for (Map<String, String> rankDate : rankDates) {
                // 각 rankDate 맵에서 "date" 키의 값을 가져와 uniqueDatesSet에 추가합니다.
                uniqueDatesSet.add(rankDate.get("date"));
            }
        }
        // 중복 제거된 날짜를 리스트로 변환하고, 필요하다면 정렬
        List<String> sortedUniqueDates = uniqueDatesSet.stream().sorted().toList();

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(keywordList);

        model.addAttribute("uniqueDates", sortedUniqueDates);
        model.addAttribute("post", post);
        model.addAttribute("keywordList", keywordList);
        model.addAttribute("json", json);
        return "blog/blogDetail";
    }
    @PatchMapping(value = "/blog/delete")
    public ResponseEntity<?> deletePosts(@RequestBody List<String> postNos) throws Exception {
        if (postNos.isEmpty()) {
            return ResponseEntity.badRequest().body("선택된 게시물이 없습니다.");
        }
        blogService.deletePosts(postNos);
        return ResponseEntity.ok().body(postNos.size() + "개의 게시물이 삭제되었습니다.");
    }

    @PostMapping("/blog/keyword")
    public ResponseEntity<?> saveKeywords(@RequestBody RequestBlogDto requestBlogDto) throws Exception {
         blogService.saveKeywords(requestBlogDto);
        return new ResponseEntity<>("키워드가 수정 되었습니다",HttpStatus.OK);
    }


}