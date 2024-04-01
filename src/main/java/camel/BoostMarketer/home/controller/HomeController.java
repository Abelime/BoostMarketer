package camel.BoostMarketer.home.controller;

import camel.BoostMarketer.blog.service.BlogService;
import camel.BoostMarketer.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BlogService blogService;

    private final KeywordService keywordService;

    @GetMapping(value = "/")
    public String homePage(Model model) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        List<HashMap<String, Object>> resultList = blogService.selectRecentPost(email);
        HashMap<String, Object> blogMap = blogService.selectPostCntInfo(email);
        HashMap<String, Object> keywordMap = keywordService.selectKeywordCntInfo(email);

        model.addAttribute("resultList", resultList);
        model.addAttribute("blogMap", blogMap);
        model.addAttribute("keywordMap", keywordMap);
        model.addAttribute("id", email);
        model.addAttribute("role", role);

        return "pages/dashboard";
    }

    @ResponseBody
    @GetMapping(value = "/admin")
    public String admin() {
        return "admin page";
    }

}
