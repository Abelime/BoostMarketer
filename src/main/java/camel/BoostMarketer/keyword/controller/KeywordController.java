package camel.BoostMarketer.keyword.controller;

import camel.BoostMarketer.blog.service.BlogService;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class KeywordController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    private final KeywordService keywordService;

    @GetMapping(value = "/keyword")
    public String blogForm(Model model,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                           @RequestParam(value = "category", defaultValue = "0") int category,
                           @RequestParam(value = "inputCategory", defaultValue = "0") int inputCategory,
                           @RequestParam(value = "sort", defaultValue = "category") String sort) throws Exception {


        Map<String, Object> resultMap = keywordService.selectKeywordInfo(page, pageSize, category, sort);

        model.addAttribute("sort", sort);
        model.addAttribute("inputCategory", inputCategory);
        model.addAttribute("category", category);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalCount", resultMap.get("keywordCount"));
        model.addAttribute("keywordRankCount", resultMap.get("keywordRankCount"));
        model.addAttribute("keywordList", resultMap.get("keywordDtoList"));
        return "pages/keyword";
    }

    @PostMapping(value = "/keyword")
    public ResponseEntity<?> registerKeyword(@RequestBody KeywordDto keywordDto) throws Exception {
        keywordService.registerKeyword(keywordDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/keyword/{keywordId}")
    public ResponseEntity<?> keywordDelete(@PathVariable("keywordId") Long keywordId) throws Exception {
        keywordService.keywordDelete(keywordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/keyword/{keywordId}")
    public ResponseEntity<?> keywordFix(@PathVariable("keywordId") Long keywordId) throws Exception {
        keywordService.keywordFix(keywordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/keyword/excelUpload")
    public ResponseEntity<?> keywordExcelUpload(@RequestParam("file") MultipartFile file) throws Exception {
        keywordService.keywordExcelUpload(file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/keyword/popup/{keywordId}")
    public String keywordPopUp(Model model,@PathVariable("keywordId") Long keywordId) throws Exception {
        String keywordName = "";
        List<HashMap<String, Object>> resultList = blogService.getRankedBlogsByKeyword(keywordId);

        if(!resultList.isEmpty()){
            keywordName = (String) resultList.get(0).get("keywordName");
        }
        model.addAttribute("keywordName", keywordName);
        model.addAttribute("resultList", resultList);
        return "common/keyword-popup";
    }


}