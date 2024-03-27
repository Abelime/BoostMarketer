package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.dto.*;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.dto.CommonBlogDto;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static camel.BoostMarketer.common.api.Crawler.*;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogMapper blogMapper;

    public void create(RequestBlogDto requestBlogDto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String postNo = requestBlogDto.getPostNo();
        String blogId = requestBlogDto.getBlogId();
        String paramUrl = "blogId=" + blogId + "&logNo=" + postNo;

        int visitorCnt = visitorCntCrawler(blogId); //방문자 수 크롤링

        Map<String, Object> postInfo = postInfoCrawler(paramUrl, postNo); //블로그 글정보
        postInfo.put("blogId", blogId);
        postInfo.put("postNo", postNo);
        BlogPostDto blogPostDto = mapper.convertValue(postInfo, BlogPostDto.class);

        Map<String, Object> postExist = blogMapper.selectPostCount(blogPostDto);//블로그 글 정보 INSERT

        if((Long)postExist.get("cnt") == 0){
            blogMapper.insertBlogPost(blogPostDto); //블로그 글 정보 INSERT
        }else if((Long)postExist.get("cnt") == 1 && postExist.get("useFlg").equals("N")){
            blogMapper.updateBlogPost(blogPostDto); //블로그 글 정보 INSERT
        }


        List<Integer> rankList = rankCrawler(requestBlogDto); //키워드 랭킹

        KeywordDto keywordDto;

//        for (int i = 0; i < rankList.size(); i++) {
//            keywordDto = new KeywordDto(requestBlogDto.getPostNo(), requestBlogDto.getKeyWord().get(i), rankList.get(i));
//            blogMapper.insertKeyword(keywordDto); //키워드 사전 등록
//            blogMapper.insertKeywordRank(keywordDto); //키워드 랭킹 등록
//        }
    }

    public List<BlogPostDto> selectPostList() throws Exception {
        return blogMapper.selectPostList();
    }

    public List<KeywordDto> selectKeyWordRank(List<BlogPostDto> blogPostDtoList) throws Exception {
        return blogMapper.selectKeyWordRank(blogPostDtoList);
    }

    public BlogPostDto selectDetailPost(String postNo) throws Exception {
        return blogMapper.selectDetailPost(postNo);
    }

    public List<Map<String, Object>> selectDetailRank(String postNo) throws Exception {
        List<KeywordDto> keywordDtoList = blogMapper.selectDetailRank(postNo);

        Map<String, List<Map<String, Object>>> map = new HashMap<>();


        for (KeywordDto keywordDto : keywordDtoList) {
            Map<String, Object> keywordMap = new HashMap<>();
            keywordMap.put("date", keywordDto.getRankDate());
//            keywordMap.put("rank", keywordDto.getKeywordRank());

            String keywordName = keywordDto.getKeywordName();
            if (!map.containsKey(keywordName)) {
                map.put(keywordName, new ArrayList<>());
            }
            map.get(keywordName).add(keywordMap);
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String keywordName : map.keySet()) {
            Map<String, Object> keywordNode = new HashMap<>();
            keywordNode.put("keywordName", keywordName);
            keywordNode.put("rankDates", map.get(keywordName));
            resultList.add(keywordNode);
        }

        return resultList;
    }

    public void deletePosts(List<String> postNos) throws Exception {
        blogMapper.deletePost(postNos);
        blogMapper.deleteKeyword(postNos);
    }

    public void saveKeywords(RequestBlogDto requestBlogDto) throws Exception {
        String blogUrl = "https://blog.naver.com/" + requestBlogDto.getBlogId() + "/" + requestBlogDto.getPostNo();
        requestBlogDto.setBlogUrl(blogUrl);

        //클라이언트로부터 받은 키워드
        List<String> receivedKeywords = requestBlogDto.getKeyWord();
        //DB저장 되어있는 키워드
        List<KeywordDto> keywordDtoList = blogMapper.selectKeywords(requestBlogDto.getPostNo());

        // DB의 기존 키워드를 Set으로 변환
        Set<String> existingKeywordSet = keywordDtoList.stream()
                .map(KeywordDto::getKeywordName)
                .collect(Collectors.toSet());


        // 클라이언트에서 새로 받은 키워드를 식별 (추가 대상)
        List<String> keywordsToAdd = receivedKeywords.stream()
                .filter(keyword -> !existingKeywordSet.contains(keyword))
                .toList();

        // 추가 대상 키워드를 DB에 추가
        if (!keywordsToAdd.isEmpty()) {
            requestBlogDto.setKeyWord(keywordsToAdd);
            List<Integer> rankList = rankCrawler(requestBlogDto); //키워드 랭킹

            KeywordDto keywordDto;

            for (int i = 0; i < rankList.size(); i++) {
//                keywordDto = new KeywordDto(requestBlogDto.getPostNo(), requestBlogDto.getKeyWord().get(i), rankList.get(i));
//                blogMapper.insertKeyword(keywordDto); //키워드 사전 등록
//                blogMapper.insertKeywordRank(keywordDto); //키워드 랭킹 등록
            }
        }


        // 클라이언트로부터 받은 키워드가 아닌 DB 내 키워드들의 ID를 한 번에 추출
        List<Long> keywordIdsToDelete = keywordDtoList.stream()
                .filter(keywordDto -> !receivedKeywords.contains(keywordDto.getKeywordName()))
                .map(KeywordDto::getKeywordId) // 필터링된 KeywordDto에서 ID를 가져옵니다.
                .toList();

        // 삭제 대상 키워드를 DB에서 삭제
        if (!keywordIdsToDelete.isEmpty()) {
            blogMapper.deleteKeyword2(requestBlogDto.getPostNo(), keywordIdsToDelete);
        }


    }

    public List<BlogDto> selectBlogInfo() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return blogMapper.selectBlogInfo(email);
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
}
