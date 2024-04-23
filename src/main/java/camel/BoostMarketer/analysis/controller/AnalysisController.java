package camel.BoostMarketer.analysis.controller;

import camel.BoostMarketer.analysis.service.AnalysisService;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AnalysisController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AnalysisService analysisService;

    private final KeywordMapper keywordMapper;

    @GetMapping(value = "/analyze-index")
    public String analyzePage() throws Exception {
        return "pages/analyze-index";
    }

    @GetMapping(value = "/keyword-analyze")
    public String registerTrends(@RequestParam(value = "keyword") String keyword, Model model) throws Exception {
        Map<String, Object> resultMap = analysisService.getAnalyzeDate(keyword);
        List<HashMap<String, String>> sectionList = (List<HashMap<String, String>>) resultMap.get("sectionList");
        List<HashMap<String, String>> blogList = (List<HashMap<String, String>>) resultMap.get("blogList");
        List<KeywordDto> relatedkeywordList = (List<KeywordDto>) resultMap.get("relatedkeywordList");
        KeywordDto keywordDto = (KeywordDto) resultMap.get("keywordDto");

//        analysisService.searchTrends(request.getParameter("keyword"),request.getParameter("startDate"),request.getParameter("endDate"))

        model.addAttribute("sectionList", sectionList);
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

    @GetMapping(value = "/trends2")
    public String registerTrends2(Model model, HttpServletRequest request) throws Exception {
        if (request.getParameter("keyword") != null && !request.getParameter("keyword").equals("")) {
            model.addAttribute("dataList",
                    analysisService.searchTrends2(request.getParameter("keyword"), request.getParameter("startYear"), request.getParameter("startMonth"), request.getParameter("endYear"), request.getParameter("endMonth")));
        }
        if (request.getParameter("keyword") != null && !request.getParameter("keyword").equals("")) {
//            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
//            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
        }
        model.addAttribute("keyword", request.getParameter("keyword"));
        //return new ResponseEntity<>(HttpStatus.CREATED);
        return "keyword-analyze";
    }

    @PostMapping(value = "/trendsChart")
    public ResponseEntity<?> registerTrendsChart(@RequestBody Map<String, String> requestData, HttpServletRequest request) throws Exception {
        List<HashMap<String, Object>> responseData = new ArrayList<>();

        if ((requestData.get("dateType_Sel")).equals("daily")) {
            responseData = analysisService.searchTrends(requestData.get("keyword"), requestData.get("startDate"), requestData.get("endDate"));
        } else {
            responseData = analysisService.searchTrends2(requestData.get("keyword"), requestData.get("startYear"), requestData.get("startMonth"), requestData.get("endYear"), requestData.get("endMonth"));
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

//    @GetMapping(value = "/test")
//    public ResponseEntity<?> test() throws Exception {
//       List<String> keywords = keywordMapper.test();
//       Map<String, String> headers = Crawler.getHeaders();
//       int index = 1;
//       for (String keyword : keywords) {
//           try {
//               Crawler.totalSearchCrawler(null, keyword);
//               Random random = new Random();
//               int sleepTime = 400 + random.nextInt(200);
//               Thread.sleep(sleepTime);
//
//               Crawler.blogTabCrawler(null, keyword);
//               Thread.sleep(sleepTime);
//               logger.info(index+ ": 성공");
//           } catch (Exception e) {
//               logger.error("크롤링 1차 차단!! [키워드]: " + keyword);
//               Map<String, String> headerData = Crawler.headerData;
//               for (String s : headerData.keySet()) {
//                   logger.error("헤더데이터 : " + headerData.get(s));
//               }
//               Thread.sleep(10000); // Sleep for 10 seconds
//               headers.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
//               handleHttpStatusException(keyword, index);
//           }
//           index++;
//       }
//
//       return new ResponseEntity<>(HttpStatus.OK);
//    }

}
