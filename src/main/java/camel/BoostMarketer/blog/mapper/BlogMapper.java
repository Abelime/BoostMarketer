package camel.BoostMarketer.blog.mapper;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.dto.keywordDto;

import java.util.List;


public interface BlogMapper {

    public int insertBlogPost(BlogPostDto blogPostDto) throws Exception;

    public int insertKeyword(keywordDto keyWordDto) throws Exception;

    public int insertKeywordRank(keywordDto keyWordDto) throws Exception;

    public List<BlogPostDto> selectPostList() throws Exception;

    public List<keywordDto> selectKeyWordRank(List<BlogPostDto> blogPostDtoList)throws Exception;
}
