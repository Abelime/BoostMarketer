package camel.BoostMarketer.keyword.mapper;

import camel.BoostMarketer.keyword.dto.KeywordDto;
import org.apache.ibatis.annotations.Param;

public interface KeywordMapper {
    Long registerKeywordDict(KeywordDto keywordDto) throws Exception;

    void registerUserKeyword(@Param("keywordDto") KeywordDto keywordDto, @Param("email") String email) throws Exception;

}
