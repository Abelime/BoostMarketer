package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.api.NaverBlogApi;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static camel.BoostMarketer.common.api.Crawler.*;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogMapper blogMapper;
    private final KeywordMapper keywordMapper;
    private final NaverBlogApi naverBlogApi;

    public Map<String, Object> selectBlogInfo(int page, int pageSize, String sort) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int offset = (page - 1) * pageSize;
        RowBounds rowBounds = new RowBounds(offset, pageSize);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        int blogTotalCount = blogMapper.selectBlogCnt(email);
        List<BlogDto> blogDtoList = blogMapper.selectBlogInfo(email, sort, rowBounds);

        // 날짜 형식을 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 가장 최신 날짜와 시간을 저장할 변수 초기화
        LocalDateTime latestDateTime = null;

        int totalPostCnt = 0;
        for (BlogDto blogDto : blogDtoList) {
            totalPostCnt += blogDto.getPostCnt();

            LocalDateTime dateTime = LocalDateTime.parse(blogDto.getUpdateDt(), formatter);
            if (latestDateTime == null || dateTime.isAfter(latestDateTime)) {
                latestDateTime = dateTime;
            }
        }

        String lastUpdateDt = "";
        if (latestDateTime != null) {
            lastUpdateDt = latestDateTime.format(formatter);
        }


        resultMap.put("lastUpdateDt", lastUpdateDt);
        resultMap.put("blogDtoList", blogDtoList);
        resultMap.put("totalPostCnt", totalPostCnt);
        resultMap.put("totalBlogCount", blogTotalCount);

        return resultMap;
    }

    public void registerUrl(String blogId) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        BlogDto checkBlogDto = blogMapper.checkIfBlogExists(blogId);

        if (checkBlogDto == null) { //등록 되어 있지 않은 블로그일때
            BlogDto blogDto = blogInfoCrawler(blogId); //블로그 정보

            List<BlogPostDto> blogPostDtoList = allPostCrawler(Collections.singletonList(blogId), null); //블로그 글

            // 누락 여부 체크(30개)
            int missingCheckCount = 0;

            if(blogPostDtoList.size() > 30){
                missingCheckCount = 30;
            }else{
                missingCheckCount = blogPostDtoList.size();
            }

            for (int i = 0; i < missingCheckCount; i++) {
                BlogPostDto blogPostDto = blogPostDtoList.get(i);
                String postNo = blogPostDto.getPostNo();
                String postTitle = blogPostDto.getPostTitle();
                String date = blogPostDto.getPostDate();

                String responseData = naverBlogApi.blogMissingCheckApi1(postTitle, date);

                JSONObject jsonObject = new JSONObject(responseData);
                JSONObject result = jsonObject.getJSONObject("result");
                JSONArray searchList = result.getJSONArray("searchList");

                int missingFlag = 0;

                if (searchList.isEmpty()) {
                    missingFlag = 1;
                } else {
                    for (int y = 0; y < searchList.length(); y++) {
                        JSONObject item = searchList.getJSONObject(y);
                        String postUrl = item.getString("postUrl");
                        if (postUrl.contains(postNo)) {
                            missingFlag = 0;
                            break;
                        }else{
                            missingFlag = 1;
                        }
                    }
                }

                if(missingFlag == 1){
                    String responseData2 = naverBlogApi.blogMissingCheckApi2(postTitle, date);

                    JSONObject jsonObject2 = new JSONObject(responseData2);
                    JSONObject result2 = jsonObject2.getJSONObject("result");
                    JSONArray searchList2 = result2.getJSONArray("searchList");

                    for (int y = 0; y < searchList2.length(); y++) {
                        JSONObject item = searchList2.getJSONObject(y);
                        String postUrl = item.getString("postUrl");
                        if (postUrl.contains(postNo)) {
                            missingFlag = 0;
                            break;
                        }
                    }
                }

                blogPostDto.setMissingFlag(missingFlag);
            }


            blogMapper.registerBlog(blogDto, email);
            blogMapper.registerPosts(blogPostDtoList);
        } else { //등록 되어 있는 블로그 일때
            blogMapper.registerBlog(checkBlogDto, email);
        }

    }

    public void updateBlog() throws Exception {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<BlogPostDto> lastPostNoList = blogMapper.selectLastPostNoList(email);

        List<String> blogIdList = lastPostNoList.stream()
                .map(BlogPostDto::getBlogId)
                .collect(Collectors.toList());


        List<BlogPostDto> blogPostDtoList = allPostCrawler(blogIdList, lastPostNoList);

        for (int i = 0; i < blogPostDtoList.size(); i++) {
            BlogPostDto blogPostDto = blogPostDtoList.get(i);
            String postNo = blogPostDto.getPostNo();
            String postTitle = blogPostDto.getPostTitle();
            String date = blogPostDto.getPostDate();

            String responseData = naverBlogApi.blogMissingCheckApi1(postTitle, date);

            JSONObject jsonObject = new JSONObject(responseData);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray searchList = result.getJSONArray("searchList");

            int missingFlag = 0;

            if (searchList.isEmpty()) {
                missingFlag = 1;
            } else {
                for (int y = 0; y < searchList.length(); y++) {
                    JSONObject item = searchList.getJSONObject(y);
                    String postUrl = item.getString("postUrl");
                    if (postUrl.contains(postNo)) {
                        missingFlag = 0;
                        break;
                    } else {
                        missingFlag = 1;
                    }
                }
            }

            if (missingFlag == 1) {
                String responseData2 = naverBlogApi.blogMissingCheckApi2(postTitle, date);

                JSONObject jsonObject2 = new JSONObject(responseData2);
                JSONObject result2 = jsonObject2.getJSONObject("result");
                JSONArray searchList2 = result2.getJSONArray("searchList");

                for (int y = 0; y < searchList2.length(); y++) {
                    JSONObject item = searchList2.getJSONObject(y);
                    String postUrl = item.getString("postUrl");
                    if (postUrl.contains(postNo)) {
                        missingFlag = 0;
                        break;
                    }
                }
            }


            blogPostDto.setMissingFlag(missingFlag);
        }

        if (!blogPostDtoList.isEmpty()) {
            blogMapper.registerPosts(blogPostDtoList);
        }

        //삭제된 게시글 DB에서 삭제
        for (BlogPostDto lastPostNoDto : lastPostNoList) {
            List<String> naverPostNoList = checkDeletePost(lastPostNoDto);
            if(!naverPostNoList.isEmpty()){
                List<String> dbPostNoList = blogMapper.selectPostNoByBlogId(lastPostNoDto.getBlogId());
                dbPostNoList.removeAll(naverPostNoList);
                blogMapper.deleteBlogPostByPostId(dbPostNoList);
                keywordMapper.deleteKeywordRankByPostId(dbPostNoList);
            }

        }

        blogMapper.allBlogUpdatedAt(email);

    }

    public void deleteBlog(String blogId) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        blogMapper.deleteBlog(blogId, email);

        BlogDto checkBlogDto = blogMapper.checkIfBlogExists(blogId);

        if (checkBlogDto == null) {
            blogMapper.deleteBlogPost(blogId);
        }
    }

    public Map<String, Object> selectRecentPost(String email, int page, int pageSize, String sort) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int offset = (page - 1) * pageSize;
        RowBounds rowBounds = new RowBounds(offset, pageSize);

        int recentPostCount = blogMapper.selectRecentPostCnt(email);
        List<HashMap<String, Object>> resultList = blogMapper.selectRecentPost(email, sort, rowBounds);

        resultMap.put("resultList", resultList);
        resultMap.put("totalCount", recentPostCount);
        return resultMap;
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

    public HashMap<String, Object> getMissingPostByBlog(String blogId) throws Exception {
        HashMap<String, Object> resultMap = new HashMap<>();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BlogDto blogInfoDto = blogMapper.selectBlogInfoOne(blogId);
        List<BlogPostDto> blogPostDtos = blogMapper.selectMissingPostByBlogId(blogId);

        resultMap.put("blogInfoDto", blogInfoDto);
        resultMap.put("blogPostDtos", blogPostDtos);

        return resultMap;
    }

    public BlogPostDto selectPostInfo(String postNo) throws Exception {
        return blogMapper.selectPostByPostNo(postNo);
    }

//    public void updateBlogMissingPost() {
//        List<String> blogIds = blogMapper.adminTest1();
//        for (String blogId : blogIds) {
//            List<BlogPostDto> blogPostDtos = blogMapper.adminTest(blogId);
//
//            for (int i = 0; i < blogPostDtos.size(); i++) {
//                BlogPostDto blogPostDto = blogPostDtos.get(i);
//                String postNo = blogPostDto.getPostNo();
//                String postTitle = blogPostDto.getPostTitle();
//
//                String responseData = naverBlogApi.blogMissingCheckApi(postTitle);
//
//                JSONObject jsonObject = new JSONObject(responseData);
//                JSONObject result = jsonObject.getJSONObject("result");
//                JSONArray searchList = result.getJSONArray("searchList");
//
//                int missingFlag = 0;
//
//                if (searchList.isEmpty()) {
//                    missingFlag = 1;
//                } else {
//                    for (int y = 0; y < searchList.length(); y++) {
//                        JSONObject item = searchList.getJSONObject(y);
//                        String postUrl = item.getString("postUrl");
//                        if (postUrl.contains(postNo)) {
//                            break;
//                        }else{
//                            missingFlag = 1;
//                        }
//                    }
//                }
//
//                blogPostDto.setMissingFlag(missingFlag);
//            }
//
//            blogMapper.adminTest3(blogPostDtos);
//        }
//
//    }
}
