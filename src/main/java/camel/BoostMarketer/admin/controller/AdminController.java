package camel.BoostMarketer.admin.controller;

import camel.BoostMarketer.admin.dto.AdminDto;
import camel.BoostMarketer.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;

    @PostMapping(value = "/admin")
    public ResponseEntity<?> registerUser(@RequestBody AdminDto adminDto) throws Exception {
        logger.trace("Trace Level 테스트");
        logger.debug("DEBUG Level 테스트");
        logger.info("INFO Level 테스트");
        logger.warn("Warn Level 테스트");
        logger.error("ERROR Level 테스트");
        adminService.register(adminDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
