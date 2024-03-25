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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class KeywordController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlogService blogService;

    private final KeywordService keywordService;

    @GetMapping(value = "/keyword")
    public String blogForm(Model model) throws Exception {
        List<KeywordDto> keywordDtoList = keywordService.selectKeywordInfo();

        model.addAttribute("keywordList", keywordDtoList);
        return "pages/keyword";
    }

    @PostMapping(value = "/keyword")
    public ResponseEntity<?> registerKeyword(@RequestBody KeywordDto keywordDto) throws Exception {
        keywordService.registerKeyword(keywordDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/keyword/excelUpload")
    public ResponseEntity<?> keywordExcelUpload(@RequestParam("file") MultipartFile file) throws Exception {
        keywordService.keywordExcelUpload(file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}