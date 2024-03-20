package camel.BoostMarketer.blog.mapper;

import camel.BoostMarketer.blog.dto.BlogDto;
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

    public void registerPosts(List<BlogPostDto> blogPostDtoList) throws Exception;

    public void registerBlog(@Param("blogDtoList") List<BlogDto> blogDtoList, @Param("email") String email) throws Exception;

    public List<BlogDto> selectBlogInfo(String email) throws Exception;

    public String selectLastPostNo(String blogId) throws Exception;

    public void blogUpdatedAt(String blogId) throws Exception;

    public void deleteBlog(String blogId) throws Exception;

    public void deleteBlogPost(String blogId) throws Exception;
}
