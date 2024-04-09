package camel.BoostMarketer.common.api;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class NaverSearchTrendsApi {

    private final String clientId = "bJ0En5s3dtcOMl5glNR7"; //애플리케이션 클라이언트 아이디
    private final String clientSecret = "51WKT5ZwvL"; //애플리케이션 클라이언트 시크릿

    public List<HashMap<String, Object>> apiAccess(String keyword, String startDate, String endDate) {

        String apiUrl = "https://openapi.naver.com/v1/datalab/search";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        requestHeaders.put("Content-Type", "application/json");

        String requestBody = "{\"startDate\":\""+startDate+"\"," +
                "\"endDate\":\""+endDate+"\"," +
                "\"timeUnit\":\"date\"," +
                "\"keywordGroups\":[{\"groupName\":\" "+keyword+"\"," + "\"keywords\":[\""+keyword+"\"]}]}";

        String responseBody = post(apiUrl, requestHeaders, requestBody);

        List<HashMap<String, Object>> dataList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultsNode = rootNode.get("results");

            Iterator<JsonNode> resultsIterator = resultsNode.elements();
            while (resultsIterator.hasNext()) {
                JsonNode resultNode = resultsIterator.next();
                JsonNode dataNode = resultNode.get("data");
                Iterator<JsonNode> dataIterator = dataNode.elements();
                while (dataIterator.hasNext()) {
                    JsonNode item = dataIterator.next();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("period", item.get("period").asText());
                    map.put("ratio", item.get("ratio").asDouble());
                    dataList.add(map);
                }
            }

            // 출력
//            for (JsonNode dataNode : dataList) {
//                Iterator<JsonNode> dataIterator = dataNode.elements();
//                while (dataIterator.hasNext()) {
//                    JsonNode item = dataIterator.next();
//                    String period = item.get("period").asText();
//                    double ratio = item.get("ratio").asDouble();
//                    System.out.println("Period: " + period + ", Ratio: " + ratio);
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }


    private static String post(String apiUrl, Map<String, String> requestHeaders, String requestBody) {
        HttpURLConnection con = connect(apiUrl);

        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestBody.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect(); // Connection을 재활용할 필요가 없는 프로세스일 경우
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body, StandardCharsets.UTF_8);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }


}
