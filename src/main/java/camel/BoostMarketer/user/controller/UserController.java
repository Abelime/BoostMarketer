package camel.BoostMarketer.user.controller;

import camel.BoostMarketer.user.dto.UserDto;
import camel.BoostMarketer.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.pw}")
    private String googleClientPw;

    @GetMapping(value = "/sign-in")
    public String index() throws Exception {
        return "/pages/sign-in";
    }


    @GetMapping(value = "/sign-up")
    public String signUpPage() throws Exception {
        return "/pages/sign-up";
    }

    @GetMapping("/checkDuplication/{email}")
    public ResponseEntity<?> checkDuplication(@PathVariable("email") String email) throws Exception {
        boolean isDuplicate = userService.checkEmailDuplication(email);
        return new ResponseEntity<>(isDuplicate, HttpStatus.OK);
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserDto userDto) throws Exception {
        int userCnt = userService.saveMember(userDto);

        if(userCnt == 1){
            return new ResponseEntity<>("회원 가입 되었습니다", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("중복 회원 입니다", HttpStatus.OK);
        }

    }
}
