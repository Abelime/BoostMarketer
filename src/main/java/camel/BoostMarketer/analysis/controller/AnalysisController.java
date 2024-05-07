package camel.BoostMarketer.analysis.controller;

import camel.BoostMarketer.analysis.service.AnalysisService;
import camel.BoostMarketer.keyword.dto.KeywordDto;
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

    @GetMapping(value = "/analyze-index")
    public String analyzePage() throws Exception {
        return "pages/analyze-index";
    }

    @GetMapping(value = "/keyword-analyze")
    public String registerTrends(@RequestParam(value = "keyword") String keyword, Model model) throws Exception {
        Map<String, Object> resultMap = analysisService.getAnalyzeDate(keyword);
        List<HashMap<String, String>> pcSectionList = (List<HashMap<String, String>>) resultMap.get("pcSectionList");
        List<HashMap<String, String>> blogList = (List<HashMap<String, String>>) resultMap.get("blogList");
        List<KeywordDto> relatedkeywordList = (List<KeywordDto>) resultMap.get("relatedkeywordList");
        List<HashMap<String, String>> mobileSectionList = (List<HashMap<String, String>>) resultMap.get("mobileSectionList");

        KeywordDto keywordDto = (KeywordDto) resultMap.get("keywordDto");

//        analysisService.searchTrends(request.getParameter("keyword"),request.getParameter("startDate"),request.getParameter("endDate"))

        model.addAttribute("totalBlogCnt", resultMap.get("totalBlogCnt"));
        model.addAttribute("monthBlogCnt", resultMap.get("monthBlogCnt"));
        model.addAttribute("blogSaturation", resultMap.get("blogSaturation"));
        model.addAttribute("pcSectionList", pcSectionList);
        model.addAttribute("mobileSectionList", mobileSectionList);
        model.addAttribute("blogList", blogList);
        model.addAttribute("relatedkeywordList", relatedkeywordList);
        model.addAttribute("keywordDto", keywordDto);
        model.addAttribute("keyword", keyword);
        return "pages/keyword-analyze";


//        if(request.getParameter("keyword")!=null&& !request.getParameter("keyword").equals("")&&
//                request.getParameter("startDate")!=null && !request.getParameter("startDate").equals("")){
//            model.addAttribute("dataList",analysisService.searchTrends(request.getParameter("keyword"),request.getParameter("startDate"),request.getParameter("endDate")));
//        }
//        if(request.getParameter("keyword")!=null && !request.getParameter("keyword").equals("")){
//            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
//            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
//        }
//        model.addAttribute("keyword",request.getParameter("keyword"));
//        //return new ResponseEntity<>(HttpStatus.CREATED);
//        return "pages/keyword-analyze";
    }

//    @GetMapping(value = "/trends2")
//    public String registerTrends2(Model model, HttpServletRequest request) throws Exception {
//        if (request.getParameter("keyword") != null && !request.getParameter("keyword").equals("")) {
//            model.addAttribute("dataList",
////                    analysisService.searchTrends2(request.getParameter("keyword"), request.getParameter("startYear"), request.getParameter("startMonth"), request.getParameter("endYear"), request.getParameter("endMonth")));
//        }
//        if (request.getParameter("keyword") != null && !request.getParameter("keyword").equals("")) {
////            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
////            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
//        }
//        model.addAttribute("keyword", request.getParameter("keyword"));
//        //return new ResponseEntity<>(HttpStatus.CREATED);
//        return "keyword-analyze";
//    }

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
}
