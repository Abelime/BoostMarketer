package camel.BoostMarketer.blog.mapper;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.dto.KeywordDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface BlogMapper {

    public void insertBlogPost(BlogPostDto blogPostDto) throws Exception;

    public void insertKeyword(KeywordDto keyWordDto) throws Exception;

    public void insertKeywordRank(KeywordDto keyWordDto) throws Exception;

    public List<BlogPostDto> selectPostList() throws Exception;

    public List<KeywordDto> selectKeyWordRank(List<BlogPostDto> blogPostDtoList) throws Exception;

    public List<KeywordDto> selectDetailRank(String postNo) throws Exception;

    public BlogPostDto selectDetailPost(String postNo) throws Exception;

    public void deletePost(List<String> postNos) throws Exception;

    public void deleteKeyword(List<String> postNos) throws Exception;

    public void deleteKeyword2(@Param("postNo") String postNo, @Param("keywordIds") List<Long> keywordIds) throws Exception;

    public List<KeywordDto> selectKeywords(String postNo) throws Exception;

    public Map<String, Object> selectPostCount(BlogPostDto blogPostDto) throws Exception;

    public void updateBlogPost(BlogPostDto blogPostDto) throws Exception;
}
