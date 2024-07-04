package camel.BoostMarketer.blog.service;

import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.mapper.BlogMapper;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverBlogApi;
import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static camel.BoostMarketer.common.api.Crawler.checkDeletePost;

@Service
@RequiredArgsConstructor
public class BlogBatchService {

    private final BlogMapper blogMapper;
    private final KeywordMapper keywordMapper;
    private final NaverBlogApi naverBlogApi;

    public void updateBlogPost() throws Exception {
        List<BlogPostDto> lastPostNoList = blogMapper.selectAllLastPostNo();

        List<String> blogIdList = lastPostNoList.stream()
                      .map(BlogPostDto::getBlogId)
                      .collect(Collectors.toList());


        List<BlogPostDto> blogPostDtoList = Crawler.allPostCrawler(blogIdList, lastPostNoList);

        if (!blogPostDtoList.isEmpty()) {
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

            blogMapper.registerPosts(blogPostDtoList);
        }

        blogMapper.allBlogUpdatedAt("");

    }

    public void deleteCheckBlogPost() throws Exception {
        List<BlogPostDto> lastPostNoList = blogMapper.selectAllLastPostNo();

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
    }

}
