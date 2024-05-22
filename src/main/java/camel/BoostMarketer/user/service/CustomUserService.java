package camel.BoostMarketer.user.service;

import camel.BoostMarketer.user.dto.CustomUserDto;
import camel.BoostMarketer.user.dto.UserDto;
import camel.BoostMarketer.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userMapper.findByEmail(username);
        if(userDto.getId() == 26 || userDto.getId() == 10 || userDto.getId() == 12){
            userDto.setRole("admin");
        }else{
            userDto.setRole("ROLE_USER");
        }
        return new CustomUserDto(userDto);

    }
}
