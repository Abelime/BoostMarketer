package camel.BoostMarketer.analysis.controller;

import camel.BoostMarketer.analysis.service.AnalysisService;
import camel.BoostMarketer.common.api.Crawler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AnalysisController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AnalysisService analysisService;

    @GetMapping(value = "/trends")
    public String registerTrends(Model model, HttpServletRequest request) throws Exception {
        if(request.getParameter("keyword")!=null&& !request.getParameter("keyword").equals("")&&
                request.getParameter("startDate")!=null && !request.getParameter("startDate").equals("")){
            model.addAttribute("dataList",analysisService.searchTrends(request.getParameter("keyword"),request.getParameter("startDate"),request.getParameter("endDate")));
        }
        if(request.getParameter("keyword")!=null && !request.getParameter("keyword").equals("")){
            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
        }
        model.addAttribute("keyword",request.getParameter("keyword"));
        //return new ResponseEntity<>(HttpStatus.CREATED);
        return "pages/trends";
    }

    @GetMapping(value = "/trends2")
    public String registerTrends2(Model model, HttpServletRequest request) throws Exception {
        if(request.getParameter("keyword")!=null&& !request.getParameter("keyword").equals("")){
            model.addAttribute("dataList",
                    analysisService.searchTrends2(request.getParameter("keyword"),request.getParameter("startYear"),request.getParameter("startMonth"),request.getParameter("endYear"),request.getParameter("endMonth")));
        }
        if(request.getParameter("keyword")!=null && !request.getParameter("keyword").equals("")){
            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
        }
        model.addAttribute("keyword",request.getParameter("keyword"));
        //return new ResponseEntity<>(HttpStatus.CREATED);
        return "pages/trends";
    }

    @PostMapping(value = "/trendsChart")
    public ResponseEntity<?> registerTrendsChart(@RequestBody Map<String, String> requestData, HttpServletRequest request) throws Exception {
        List<HashMap<String, Object>> responseData = new ArrayList<>();

        if((requestData.get("dateType_Sel")).equals("daily")){
            responseData = analysisService.searchTrends(requestData.get("keyword"),requestData.get("startDate"),requestData.get("endDate"));
        }else{
            responseData = analysisService.searchTrends2(requestData.get("keyword"),requestData.get("startYear"),requestData.get("startMonth"),requestData.get("endYear"),requestData.get("endMonth"));
        }
        return new ResponseEntity<>(responseData,HttpStatus.OK);
    }


}
