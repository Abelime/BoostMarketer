package camel.BoostMarketer.analysis.controller;

import camel.BoostMarketer.analysis.dto.NaverContentDto;
import camel.BoostMarketer.analysis.dto.RelatedKeywordDto;
import camel.BoostMarketer.analysis.service.AnalysisService;
import camel.BoostMarketer.keyword.controller.KeywordController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080/") // 특정 도메인만 허용
public class AnalysisController {

    private final AnalysisService analysisService;
    private final KeywordController keywordController;

    @GetMapping(value = "/analyze-index")
    public String analyzePage() throws Exception {
        return "pages/analyze-index";
    }

    @GetMapping(value = "/keyword-analyze")
    public String registerTrends(@RequestParam(value = "keyword") String keyword, Model model) throws Exception {
        Map<String, Object> resultMap = analysisService.getAnalyzeData(keyword);
        List<HashMap<String, String>> pcSectionList = (List<HashMap<String, String>>) resultMap.get("pcSectionList");
        List<HashMap<String, String>> blogList = (List<HashMap<String, String>>) resultMap.get("blogList");
        List<HashMap<String, String>> mobileSectionList = (List<HashMap<String, String>>) resultMap.get("mobileSectionList");


        model.addAttribute("smartBlockList", resultMap.get("smartBlockList"));
        model.addAttribute("naverContentDtoList", resultMap.get("naverContentDtoList"));
        model.addAttribute("smartBlockHrefList", resultMap.get("smartBlockHrefList"));
        model.addAttribute("totalBlogCnt", resultMap.get("totalBlogCnt"));
        model.addAttribute("monthBlogCnt", resultMap.get("monthBlogCnt"));
        model.addAttribute("blogSaturation", resultMap.get("blogSaturation"));
        model.addAttribute("keywordDto", resultMap.get("keywordDto"));
        model.addAttribute("pcSectionList", pcSectionList);
        model.addAttribute("mobileSectionList", mobileSectionList);
        model.addAttribute("blogList", blogList);
        model.addAttribute("keyword", keyword);
        return "pages/keyword-analyze";
    }

    @GetMapping(value = "/trendsChart")
    public ResponseEntity<?> registerTrendsChart(HttpServletRequest request) throws Exception {
        List<HashMap<String, Object>> responseData = new ArrayList<>();
        String keyword = request.getParameter("keyword");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        if ((request.getParameter("dateType_Sel")).equals("일간")) {
            responseData = analysisService.searchTrends(keyword, startDate, endDate);
        } else {
            responseData = analysisService.searchTrends2(keyword, startDate, endDate);
        }
        // 응답의 Content-Type을 명확히 JSON으로 설정
        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_JSON) // JSON을 명확히 지정
                             .body(responseData);
    }

    @GetMapping(value = "/related-keyword")
    public ResponseEntity<?> getRelatedKeywords(@RequestParam(value = "keyword") String keyword) throws Exception {
        List<RelatedKeywordDto> responseData = analysisService.findRelatedKeywords(keyword);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON) // JSON을 명확히 지정
                .body(responseData);
    }

    @GetMapping(value = "/smart-block-contents")
    public ResponseEntity<?> smartBlockContent(@RequestParam(value = "link") String link) throws Exception {
        List<NaverContentDto> responseData = analysisService.findNaverContents(link);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON) // JSON을 명확히 지정
                .body(responseData);
    }
}
