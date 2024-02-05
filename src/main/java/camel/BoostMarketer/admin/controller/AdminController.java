package camel.BoostMarketer.admin.controller;

import camel.BoostMarketer.admin.dto.AdminDto;
import camel.BoostMarketer.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping(value = "/admin")
    public ResponseEntity<?> registerUser(@RequestBody AdminDto adminDto) throws Exception {
        adminService.register(adminDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
