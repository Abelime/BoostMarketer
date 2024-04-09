package camel.BoostMarketer.analysis.controller;

import camel.BoostMarketer.analysis.service.AnalysisService;
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

        model.addAttribute("dataList",analysisService.searchTrends(request.getParameter("startDate"),request.getParameter("endDate")));
        //return new ResponseEntity<>(HttpStatus.CREATED);
        return "pages/test";
    }
}
