package camel.BoostMarketer.user.mapper;

import camel.BoostMarketer.user.dto.UserDto;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {


    /**
     * 회원 정보 저장 (회원가입)
     */
    public int save(UserDto dto) throws Exception;

    /**
     * 중복 회원 검증 (회원가입)
     */
    public int findByUser(@Param("email") String email);


    public UserDto findByEmail(@Param("email") String email);

}
