package camel.BoostMarketer.experience.controller;

import camel.BoostMarketer.experience.dto.ExperienceDto;
import camel.BoostMarketer.experience.dto.ExperienceResponseDto;
import camel.BoostMarketer.experience.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class ExperienceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExperienceService experienceService;

    @GetMapping("/experience")
    public String experience(Model model,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                             @RequestParam(value = "searchKeyword", defaultValue = "") String searchKeyword) throws Exception {

        Map<String, Object> resultMap = experienceService.selectExperienceInfo(page, pageSize, searchKeyword);

        model.addAttribute("totalCount", resultMap.get("totalCount"));
        model.addAttribute("experienceResponseDtoList", resultMap.get("experienceResponseDtoList"));
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        return "pages/experience/index";
    }

    @GetMapping("/experience/new")
    public String showExperienceForm() {
        return "pages/experience/createForm";
    }

    @PostMapping("/experience")
    public ResponseEntity<?> registerExperience(@RequestParam("title") String title,
                                                @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                                                @RequestParam("experienceType") String experienceType,
                                                @RequestParam("links") List<String> links,
                                                @RequestParam("keywords") List<String> keywords) throws Exception {

        ExperienceDto experienceDto = new ExperienceDto();
        experienceDto.setTitle(title);
        experienceDto.setType(experienceType);
        experienceDto.setLinks(links);
        experienceDto.setKeywords(keywords);
        if (thumbnail != null && !thumbnail.isEmpty()) {
            experienceDto.setThumbnail(thumbnail.getBytes());
        }
        experienceService.saveExperience(experienceDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/experience/{id}")
    public String getExperienceDetail(@PathVariable("id") Long id, Model model)  throws Exception{
        ExperienceResponseDto experienceResponseDto = experienceService.getExperienceDetail(id);

        model.addAttribute("experienceResponseDto", experienceResponseDto);
        return "pages/experience/detail";
    }
}

