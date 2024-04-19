package camel.BoostMarketer.analysis.controller;

import camel.BoostMarketer.analysis.service.AnalysisService;
import camel.BoostMarketer.common.api.Crawler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AnalysisController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AnalysisService analysisService;

    @GetMapping(value = "/trends")
    public String registerTrends(Model model, HttpServletRequest request) throws Exception {
        if(request.getParameter("startDate")!=null && !request.getParameter("startDate").equals("")){
            model.addAttribute("dataList",analysisService.searchTrends(request.getParameter("startDate"),request.getParameter("endDate")));
        }
        if(request.getParameter("keyword")!=null && !request.getParameter("keyword").equals("")){
            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
        }

        //return new ResponseEntity<>(HttpStatus.CREATED);
        return "pages/trends";
    }

    @GetMapping(value = "/trends2")
    public String registerTrends2(Model model, HttpServletRequest request) throws Exception {

        model.addAttribute("dataList",
                analysisService.searchTrends2(request.getParameter("startYear"),request.getParameter("startMonth"),request.getParameter("endYear"),request.getParameter("endMonth")));
        if(request.getParameter("keyword")!=null && !request.getParameter("keyword").equals("")){
            model.addAttribute("dataList2", Crawler.sectionSearchCrawler(request.getParameter("keyword")));
            model.addAttribute("dataList3", Crawler.blogtabPostingCrawler(request.getParameter("keyword")));
        }
        //return new ResponseEntity<>(HttpStatus.CREATED);
        return "pages/test";
    }

}
