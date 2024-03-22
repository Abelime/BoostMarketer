package camel.BoostMarketer.keyword.controller;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.service.BlogService;
import camel.BoostMarketer.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class KeywordController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    private final KeywordService keywordService;

    @GetMapping(value = "/keyword")
    public String blogForm(Model model) throws Exception {
        List<BlogDto> blogDtoList = blogService.selectBlogInfo();

        int totalPostCnt = 0;

        for (BlogDto blogDto : blogDtoList) {
            totalPostCnt += blogDto.getPostCnt();
        }

        model.addAttribute("totalPostCnt", totalPostCnt);
        model.addAttribute("blogList", blogDtoList);
        return "pages/keyword";
    }

}