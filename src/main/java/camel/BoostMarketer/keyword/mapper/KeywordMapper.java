package camel.BoostMarketer.keyword.mapper;

import camel.BoostMarketer.keyword.dto.KeywordDto;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface KeywordMapper {
    void registerKeywordDict(KeywordDto keywordDto) throws Exception;

    void registerUserKeyword(@Param("keywordDto") KeywordDto keywordDto, @Param("email") String email) throws Exception;

    void registerKeywordRank(List<KeywordDto> keywordDtoList) throws Exception;

    List<KeywordDto> selectKeywordInfo(String email) throws Exception;

    void deleteKeyDict(HashMap<String, Object> map) throws Exception;

    void deleteKeyRank1(HashMap<String, Object> map) throws Exception;

    void deleteKeyRank2(HashMap<String, Object> map) throws Exception;

    void deleteUserKey(HashMap<String, Object> map) throws Exception;
}
