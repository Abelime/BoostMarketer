package camel.BoostMarketer.keyword.controller;

import camel.BoostMarketer.blog.service.BlogService;
import camel.BoostMarketer.common.util.ExcelUtil;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;


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
                           @RequestParam(value = "filterCategory", defaultValue = "0") int filterCategory,
                           @RequestParam(value = "inputCategory", defaultValue = "0") int inputCategory,
                           @RequestParam(value = "sort", defaultValue = "general") String sort,
                           @RequestParam(value = "searchKeyword", defaultValue = "") String searchKeyword) throws Exception {


        Map<String, Object> resultMap = keywordService.selectKeywordsInfo(page, pageSize, filterCategory, sort, searchKeyword);
        List<KeywordDto> categoryList = keywordService.selectCategoryInfo();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        model.addAttribute("role", role);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("sort", sort);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("inputCategory", inputCategory);
        model.addAttribute("filterCategory", filterCategory);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalCount", resultMap.get("keywordCount"));
        model.addAttribute("keywordRankCount", resultMap.get("keywordRankCount"));
        model.addAttribute("keywordList", resultMap.get("keywordDtoList"));
        model.addAttribute("completeDate", resultMap.get("completeDate"));
        return "pages/keyword";
    }

    @PostMapping(value = "/keyword")
    public ResponseEntity<?> registerKeyword(@RequestBody KeywordDto keywordDto) throws Exception {
        keywordService.registerKeyword(keywordDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/keyword/{keywordId}")
    public ResponseEntity<?> keywordFix(@PathVariable("keywordId") Long keywordId) throws Exception {
        keywordService.keywordFix(keywordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/keyword-excelUpload")
    public ResponseEntity<?> keywordExcelUpload(@RequestParam("file") MultipartFile file) throws Exception {
        keywordService.keywordExcelUpload(file);
        return new ResponseEntity<>(HttpStatus.OK);
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

    @PutMapping("/keywords")
    public ResponseEntity<?> changeCategory(@RequestBody Map<String, Object> payload) throws Exception {
        int category = (int) payload.get("category");
        List<String> keywordIdList = (List<String>) payload.get("checkboxValues");
        keywordService.changeCategory(category, keywordIdList);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/keyword")
    public ResponseEntity<?> deleteKeywords(@RequestBody Map<String, List<Long>> param) throws Exception {
        List<Long> keywordIds = param.get("keywordIds");
        keywordService.keywordDelete(keywordIds);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/keyword/category")
    public ResponseEntity<?> updateCategory(@RequestBody Map<String, Object> param) throws Exception {
        List<Integer> categoryIdList = (List<Integer>) param.get("categoryId");
        List<String> categoryNameList = (List<String>) param.get("categoryName");

        keywordService.updateCategory(categoryIdList, categoryNameList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/excel")
    public ResponseEntity<byte[]> downloadExcel() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=keyword_excel.xlsx");
        List<String> categories = keywordService.selectCategoryInfo().stream()
                                                                     .map(KeywordDto::getCategoryName)
                                                                     .collect(Collectors.toList());

        ByteArrayInputStream in = ExcelUtil.createExcelFile(categories);

        return ResponseEntity.ok()
                             .headers(headers)
                             .body(in.readAllBytes());
    }

    @PutMapping("/keyword-update")
    public ResponseEntity<?> updateKeyword() throws Exception {
        keywordService.updateKeyword();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}