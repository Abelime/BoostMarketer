package camel.BoostMarketer.common.api;// 네이버 검색 API 예제 - 블로그 검색

import camel.BoostMarketer.common.util.DateUtil;
import camel.BoostMarketer.experience.dto.ExperienceLinkDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class NaverBlogApi {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public String blogTotalCountApi(String keyword) {
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://section.blog.naver.com/ajax/SearchList.naver?endDate=&startDate=&type=post&keyword=" + keyword;
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Referer", "https://section.blog.naver.com/BlogHome.naver?");
//        requestHeaders.put("X-Naver-Client-Id", clientId);
//        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL, requestHeaders);
        responseBody = responseBody.replace(")]}',", "");
        return responseBody;
    }

    public String blogMonthCountApi(String keyword) {
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        LocalDate currentDate = LocalDate.now();
        LocalDate sameDayLastMonth = currentDate.minusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDate = sameDayLastMonth.format(formatter);    //당월
        String endDate = currentDate.format(formatter);     //전월

        String apiURL = "https://section.blog.naver.com/ajax/SearchList.naver?endDate=" + endDate + "&startDate=" + startDate + "&type=post&keyword=" + keyword;
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Referer", "https://section.blog.naver.com/BlogHome.naver?");
        String responseBody = get(apiURL, requestHeaders);
        responseBody = responseBody.replace(")]}',", "");

        return responseBody;
    }

    public String relatedKeywordListApi(String keyword) {
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://section.blog.naver.com/ajax/RelatedKeywordList.naver?keyword=" + keyword;
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Referer", "https://section.blog.naver.com/BlogHome.naver?");
        String responseBody = get(apiURL, requestHeaders);
        responseBody = responseBody.replace(")]}',", "");

        return responseBody;
    }

    public String autoCompleteKeywordListApi(String keyword) {
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://ac.search.naver.com/nx/ac?q=" + keyword + "&st=100";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Referer", "https://www.naver.com/");
        String responseBody = get(apiURL, requestHeaders);

        return responseBody;
    }

    public String blogMissingCheckApi1(String keyword, String date){
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://section.blog.naver.com/ajax/SearchList.naver?orderBy=sim&countPerPage=49&endDate=" + date + "&startDate=" + date + "&type=post&keyword=\"" + keyword +"\"";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Referer", "https://section.blog.naver.com/BlogHome.naver?");

        String responseBody = get(apiURL, requestHeaders);
        responseBody = responseBody.replace(")]}',", "");
        return responseBody;
    }

    public String blogMissingCheckApi2(String keyword, String date){
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://section.blog.naver.com/ajax/SearchList.naver?orderBy=sim&countPerPage=49&endDate=" + date + "&startDate=" + date + "&type=post&keyword=" + keyword;
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Referer", "https://section.blog.naver.com/BlogHome.naver?");

        String responseBody = get(apiURL, requestHeaders);
        responseBody = responseBody.replace(")]}',", "");
        return responseBody;
    }

    public ExperienceLinkDto blogPostInfoApi(String blogId, String postNo) {
        ExperienceLinkDto experienceLinkDto = new ExperienceLinkDto();
        String apiURL = "https://blog.naver.com/PostViewBottomTitleListAsync.naver?blogId=" + blogId + "&logNo=" + postNo + "&isThumbnailViewType=true";
        Map<String, String> requestHeaders = new HashMap<>();
        String responseBody = get(apiURL, requestHeaders);

        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray postList = jsonObject.getJSONArray("postList");

        for (int i = 0; i < postList.length(); i++) {
            JSONObject post = postList.getJSONObject(i);
            String logNo = String.valueOf(post.get("logNo"));
            if (postNo.equals(logNo)) {
                String tagName = blogPostTagApi(blogId, postNo);

                String title = post.getString("title");
                title = HtmlUtils.htmlUnescape(title);

                String addDate = post.getString("addDate");
                Date date = DateUtil.convertToLocalDate(addDate);
                addDate = DateUtil.formatDate(date);

                experienceLinkDto.setPostTitle(title);
                experienceLinkDto.setPostDate(addDate);
                experienceLinkDto.setHashtag(tagName);
                break;
            }
        }

        return experienceLinkDto;
    }

    public String blogPostTagApi(String blogId, String postNo){
        String apiURL = "https://blog.naver.com/BlogTagListInfo.naver?blogId=" + blogId + "&logNoList=" + postNo;
        Map<String, String> requestHeaders = new HashMap<>();
        String responseBody = get(apiURL, requestHeaders);
        String tagName = "";
        JSONObject tagJsonObject = new JSONObject(responseBody);
        JSONArray tagList = tagJsonObject.getJSONArray("taglist");
        for(int i = 0; i < tagList.length(); i++){
            JSONObject tag = tagList.getJSONObject(i);
            String logNo = tag.getString("logno");
            if (postNo.equals(logNo)) {
                tagName = URLDecoder.decode(tag.getString("tagName"), StandardCharsets.UTF_8);
                break;
            }
        }
        return tagName;
    }



    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}