package camel.BoostMarketer.keyword.mapper;

import camel.BoostMarketer.keyword.dto.KeywordDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KeywordMapper {
    void registerKeywordDict(KeywordDto keywordDto) throws Exception;

    void registerUserKeyword(@Param("keywordDto") KeywordDto keywordDto, @Param("email") String email) throws Exception;

    void upsertKeywordRank(List<KeywordDto> keywordDtoList) throws Exception;

    List<KeywordDto> selectKeywordInfo(@Param("email") String email, @Param("categoryId") int filterCategory, @Param("sort") String sort, RowBounds row, @Param("searchKeyword") String searchKeyword) throws Exception;

    void deleteKeyDict(HashMap<String, Object> map) throws Exception;

    void deleteKeyRank1(HashMap<String, Object> map) throws Exception;

    void deleteKeyRank2(HashMap<String, Object> map) throws Exception;

    void deleteUserKey(HashMap<String, Object> map) throws Exception;

    HashMap<String, Object> selectKeywordCntInfo(@Param("email") String email, @Param("categoryId") int filterCategory, @Param("searchKeyword") String searchKeyword) throws Exception;

    void keywordFix(@Param("email") String email, @Param("keywordId") Long keywordId) throws Exception;

    List<HashMap<String, Object>> selectRankKeywordByPost(@Param("blogId") String blogId, @Param("email") String email) throws Exception;

    List<KeywordDto> findKeywordNameByUserId(Long userId) throws Exception;

    void updateKeywordDict(KeywordDto keywordDto) throws Exception;

    void updateKeywordRank(KeywordDto keywordDto) throws Exception;

    void deleteRankData() throws Exception;

    List<KeywordDto> selectRankKeywordByPostNo(@Param("postNo") String postNo, @Param("email") String email) throws Exception;

    void updateKeywordCategory(@Param("categoryId") int category, @Param("keywordIdList") List<String> keywordIdList) throws Exception;

    void updateCategory(Map<String, Object> param) throws Exception;

    List<KeywordDto> selectCategory(String email) throws Exception;

    List<KeywordDto> findAllKeyword() throws Exception;

    String selectCompleteDate() throws Exception;

}
