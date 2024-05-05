package camel.BoostMarketer.common.api;// 네이버 검색 API 예제 - 블로그 검색

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class NaverBlogApi {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public String apiAccess(String keyword) {
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String apiURL = "https://openapi.naver.com/v1/search/blog?query=\"" + keyword +"\"" + "&sort=sim&display=100";    // JSON 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL, requestHeaders);

        // ObjectMapper 생성
//        ObjectMapper objectMapper = new ObjectMapper();
//        int rank = 0;

//        try {
//            // JSON 문자열을 JsonNode로 변환
//            JsonNode rootNode = objectMapper.readTree(responseBody);
//            // "items" 필드 가져오기
//            JsonNode itemsNode = rootNode.get("items");
//
//            for (JsonNode jsonNode : itemsNode) {
//                rank++;
//                String link = jsonNode.get("link").toString();
//                if (link.contains(blogMap.get("blogId")) && link.contains(blogMap.get("postNo"))) {
//                    break;
//                }
//
//            }
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        return responseBody;
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