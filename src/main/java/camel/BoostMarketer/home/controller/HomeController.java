package camel.BoostMarketer.home.controller;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.service.BlogService;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BlogService blogService;

    private final KeywordService keywordService;

    @GetMapping(value = "/")
    public String homePage(Model model,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                           @RequestParam(value = "sort", defaultValue = "recentPostDate") String sort) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        Map<String, Object> resultMap = blogService.selectRecentPost(email, page, pageSize, sort);

        HashMap<String, Object> blogMap = blogService.selectPostCntInfo(email);
        HashMap<String, Object> keywordMap = keywordService.selectKeywordCntInfo(email);

        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sort", sort);
        model.addAttribute("resultList", resultMap.get("resultList"));
        model.addAttribute("totalCount", resultMap.get("totalCount"));
        model.addAttribute("blogMap", blogMap);
        model.addAttribute("keywordMap", keywordMap);
        model.addAttribute("id", email);
        model.addAttribute("role", role);

        return "pages/dashboard";
    }

    @ResponseBody
    @GetMapping(value = "/homeModal/{postNo}")
    public ResponseEntity<Map<String, Object>> homeModal(@PathVariable("postNo") String postNo) throws Exception {
        BlogPostDto blogPostDto = blogService.selectPostInfo(postNo);
        List<KeywordDto> keywordDtos = keywordService.selectKeywordInfo(postNo);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("blogPostDto", blogPostDto);
        responseData.put("keywordDtos", keywordDtos);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


}
