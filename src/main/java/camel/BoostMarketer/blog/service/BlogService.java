package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.dto.CommonBlogDto;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

import static camel.BoostMarketer.common.api.Crawler.allPostCrawler;
import static camel.BoostMarketer.common.api.Crawler.blogInfoCrawler;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogMapper blogMapper;

    private final KeywordMapper keywordMapper;

    public Map<String, Object> selectBlogInfo(int page, int pageSize, String sort) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int offset = (page - 1) * pageSize;
        RowBounds rowBounds = new RowBounds(offset, pageSize);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        int blogTotalCount = blogMapper.selectBlogCnt(email);
        List<BlogDto> blogDtoList = blogMapper.selectBlogInfo(email, sort, rowBounds);

        int totalPostCnt = 0;
        for (BlogDto blogDto : blogDtoList) {
            totalPostCnt += blogDto.getPostCnt();
        }

        resultMap.put("blogDtoList", blogDtoList);
        resultMap.put("totalPostCnt", totalPostCnt);
        resultMap.put("totalBlogCount", blogTotalCount);

        return resultMap;
    }

    public void registerUrl(String blogId) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        BlogDto checkBlogDto = blogMapper.checkIfBlogExists(blogId);

        if (checkBlogDto == null) {
            //블로그 정보
            BlogDto blogDto = blogInfoCrawler(blogId);
            //블로그 글
            List<BlogPostDto> blogPostDtoList = allPostCrawler(Collections.singletonList(blogId), null);

            blogMapper.registerBlog(blogDto, email);
            blogMapper.registerPosts(blogPostDtoList);
        } else {
            blogMapper.registerBlog(checkBlogDto, email);
        }

    }

    public void updateBlog(String blogId) throws Exception {
        List<String> blogIdList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        if (blogId.equals("ALL")) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<CommonBlogDto> commonBlogDtos = blogMapper.selectLastPostNoList(email);

            for (CommonBlogDto commonBlogDto : commonBlogDtos) {
                blogIdList.add(commonBlogDto.getBlogId());
                map.put(commonBlogDto.getBlogId(), commonBlogDto.getPostNo());
            }

            blogMapper.allBlogUpdatedAt(email);

        } else {
            String lastPostNo = blogMapper.selectLastPostNo(blogId);
            blogIdList.add(blogId);
            map.put(blogId, lastPostNo);

            blogMapper.blogUpdatedAt(blogId);
        }

        List<BlogPostDto> blogPostDtoList = allPostCrawler(blogIdList, map);

        if (!blogPostDtoList.isEmpty()) {
            blogMapper.registerPosts(blogPostDtoList);
        }

    }

    public void deleteBlog(String blogId) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        blogMapper.deleteBlog(blogId, email);

        BlogDto checkBlogDto = blogMapper.checkIfBlogExists(blogId);

        if (checkBlogDto == null) {
            blogMapper.deleteBlogPost(blogId);
        }
    }

    public List<HashMap<String, Object>> selectRecentPost(String email) throws Exception {
        return blogMapper.selectRecentPost(email);
    }

    public HashMap<String, Object> selectPostCntInfo(String email) throws Exception {
        return blogMapper.selectPostCntInfo(email);
    }

    public List<HashMap<String, Object>> getRankedBlogsByKeyword(Long keywordId) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return blogMapper.getRankedBlogsByKeyword(keywordId, email);
    }

    public HashMap<String, Object> getRankedKeywordsByBlog(String blogId) throws Exception {
        HashMap<String, Object> resultMap = new HashMap<>();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BlogDto blogInfoDto = blogMapper.selectBlogInfoOne(blogId);
        List<HashMap<String, Object>>  postInfoList = blogMapper.selectPostByBlogId(blogId, email);
        List<HashMap<String, Object>> keywordRankInfo = keywordMapper.selectRankKeywordByPost(blogId, email);

        resultMap.put("blogInfoDto", blogInfoDto);
        resultMap.put("postInfoList", postInfoList);
        resultMap.put("keywordRankInfo", keywordRankInfo);

        return resultMap;
    }
}
