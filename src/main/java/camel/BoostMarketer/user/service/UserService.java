package camel.BoostMarketer.user.service;

import camel.BoostMarketer.user.dto.UserDto;
import camel.BoostMarketer.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public int saveMember(UserDto userDto) throws Exception {
        int userCnt = userMapper.findByUser(userDto.getEmail());
        if(userCnt != 0){
            return -1;
        }

        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userCnt = userMapper.save(userDto);

        return userCnt;
    }

    public boolean checkEmailDuplication(String email) throws Exception {
        int userCnt = userMapper.findByUser(email);
        return userCnt == 1;
    }

}
