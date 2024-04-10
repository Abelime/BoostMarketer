package camel.BoostMarketer.blog.mapper;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.common.dto.CommonBlogDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.HashMap;
import java.util.List;


public interface BlogMapper {
    public void registerPosts(List<BlogPostDto> blogPostDtoList) throws Exception;

    public void registerBlog(@Param("blogDto") BlogDto blogDto, @Param("email") String email) throws Exception;

    public List<BlogDto> selectBlogInfo(@Param("email") String email, @Param("sort") String sort, RowBounds rowBounds) throws Exception;

    public String selectLastPostNo(String blogId) throws Exception;

    public void blogUpdatedAt(String blogId) throws Exception;

    public void allBlogUpdatedAt(String email) throws Exception;

    public void deleteBlog(@Param("blogId") String blogId, @Param("email") String email) throws Exception;

    public void deleteBlogPost(String blogId) throws Exception;

    public List<CommonBlogDto> selectLastPostNoList(String email) throws Exception;

    BlogDto checkIfBlogExists(String blogId) throws Exception;

    List<HashMap<String, Object>> selectRecentPost(String email) throws Exception;

    HashMap<String, Object> selectPostCntInfo(String email) throws Exception;

    List<HashMap<String, Object>> getRankedBlogsByKeyword(@Param("keywordId") Long keywordId, @Param("email") String email) throws Exception;

    int selectBlogCnt(String email) throws Exception;

    BlogDto selectBlogInfoOne(String blogId) throws Exception;

    List<HashMap<String, Object>> selectPostByBlogId(@Param("blogId") String blogId, @Param("email") String email) throws Exception;

    List<String> selectBlogIdList(String email) throws Exception;

    List<BlogPostDto> selectAllLastPostNo() throws Exception;

    List<String> findBlogIdByUserId(Long userId) throws Exception;
}
