package camel.BoostMarketer.user.mapper;

import camel.BoostMarketer.user.dto.UserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {


    /**
     * 회원 정보 저장 (회원가입)
     */
    int save(UserDto dto) throws Exception;

    /**
     * 중복 회원 검증 (회원가입)
     */
    int findByUser(@Param("email") String email)  throws Exception;


    UserDto findByEmail(@Param("email") String email);

    List<Long> findByIds() throws Exception;
}
