package camel.BoostMarketer.blog.api;// 네이버 검색 API 예제 - 블로그 검색

import camel.BoostMarketer.blog.dto.BlogDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static camel.BoostMarketer.common.ConvertBlogUrl.convertUrl;

@Component
public class ApiSearchBlog {

        private final String clientId = "WL1M5lNM2971fY_nLeOY"; //애플리케이션 클라이언트 아이디
        private final String clientSecret = "aYYZtJiEbb"; //애플리케이션 클라이언트 시크릿

    public void apiAccess(BlogDto blogDto){
        Map<String, String> blogMap = convertUrl(blogDto.getBlogUrl());

        String text = blogDto.getKeyWord();
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + text;    // JSON 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // XML 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        // ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        int rank = 1;

        try{
            // JSON 문자열을 JsonNode로 변환
            JsonNode rootNode = objectMapper.readTree(responseBody);
            // "items" 필드 가져오기
            JsonNode itemsNode = rootNode.get("items");

            for (JsonNode jsonNode : itemsNode) {
                System.out.println("jsonNode = " + jsonNode.get("link"));
                String link = jsonNode.get("link").toString();
                if(link.contains(blogMap.get("blogId")) && link.contains(blogMap.get("blogPostId"))){
                    break;
                }
                rank++;
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("순위 : " + rank);

    }


    private String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
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


    private HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private String readBody(InputStream body){
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