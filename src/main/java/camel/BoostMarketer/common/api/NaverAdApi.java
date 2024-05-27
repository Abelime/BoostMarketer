package camel.BoostMarketer.common.api;

import camel.BoostMarketer.keyword.dto.KeywordDto;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class NaverAdApi {
    private static final String API_URL = "https://api.searchad.naver.com";

    private final String path = "/keywordstool";

    @Value("${naver.ad.api.key}")
    private String API_KEY;

    @Value("${naver.ad.api.secret}")
    private String SECRET_KEY;

    @Value("${naver.ad.api.customer.Id}")
    private String CUSTOMER_ID;
    private final Logger logger = LoggerFactory.getLogger(NaverAdApi.class);
    private final OkHttpClient client = new OkHttpClient();

    public void apiAccess(KeywordDto keywordDto) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        OkHttpClient client = new OkHttpClient();

        String keyword = keywordDto.getKeywordName();
        keyword = keyword.replace(" ", "");

        logger.debug("검색량 조회 API : " + "[" + keyword + "]");

        String timestamp = String.valueOf(System.currentTimeMillis());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + path).newBuilder();
        urlBuilder.addQueryParameter("hintKeywords", keyword);
        urlBuilder.addQueryParameter("showDetail", "1");

        String url = urlBuilder.build().toString();

        String signature = generateSignature(timestamp);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", API_KEY)
                .addHeader("X-CUSTOMER", CUSTOMER_ID)
                .addHeader("X-Timestamp", timestamp)
                .addHeader("X-Signature", signature)
                .build();
        String responseBody = "";
        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new RuntimeException("API 요청 실패: " + response);
//            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                responseBody = responseBuilder.toString();

                if (!responseBody.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray keywordList = jsonObject.getJSONArray("keywordList");

                    JSONObject firstKeyword = keywordList.getJSONObject(0);

                    Object monthlyPcQcCntStr = firstKeyword.get("monthlyPcQcCnt");
                    Object monthlyMobileQcCntStr = firstKeyword.get("monthlyMobileQcCnt");

                    int monthlyPcQcCnt = parseKeywordCount(monthlyPcQcCntStr);
                    int monthlyMobileQcCnt = parseKeywordCount(monthlyMobileQcCntStr);

                    keywordDto.setMonthSearchPc(monthlyPcQcCnt);
                    keywordDto.setMonthSearchMobile(monthlyMobileQcCnt);
                    keywordDto.setTotalSearch(monthlyPcQcCnt + monthlyMobileQcCnt);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            if (responseBody.contains("429")) {
                Thread.sleep(500);
                apiAccess(keywordDto);
            }
        }
    }


    public List<KeywordDto> relatedkeywordsAcess(String keyword) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        List<KeywordDto> resultList = new ArrayList<>();

        keyword = keyword.replace(" ", "");

        logger.debug("검색량 조회 API : " + "[" + keyword + "]");

        String timestamp = String.valueOf(System.currentTimeMillis());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + path).newBuilder();
        urlBuilder.addQueryParameter("hintKeywords", keyword);
        urlBuilder.addQueryParameter("showDetail", "1");

        String url = urlBuilder.build().toString();

        String signature = generateSignature(timestamp);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", API_KEY)
                .addHeader("X-CUSTOMER", CUSTOMER_ID)
                .addHeader("X-Timestamp", timestamp)
                .addHeader("X-Signature", signature)
                .build();
        String responseBody = "";
        try (Response response = client.newCall(request).execute()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                responseBody = responseBuilder.toString();

                if (!responseBody.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray keywordList = jsonObject.getJSONArray("keywordList");

                    for (int i = 0; i < keywordList.length(); i++) {
                        KeywordDto keywordDto = new KeywordDto();
                        JSONObject keywordJson = keywordList.getJSONObject(i);
                        Object monthlyPcQcCntStr = keywordJson.get("monthlyPcQcCnt");
                        Object monthlyMobileQcCntStr = keywordJson.get("monthlyMobileQcCnt");

                        int monthlyPcQcCnt = parseKeywordCount(monthlyPcQcCntStr);
                        int monthlyMobileQcCnt = parseKeywordCount(monthlyMobileQcCntStr);
                        keywordDto.setKeywordName(keywordJson.getString("relKeyword"));
                        keywordDto.setMonthSearchPc(monthlyPcQcCnt);
                        keywordDto.setMonthSearchMobile(monthlyMobileQcCnt);
                        keywordDto.setTotalSearch(monthlyPcQcCnt + monthlyMobileQcCnt);

                        resultList.add(keywordDto);
                        if (i == 10) break; //연관 검색어 10개
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            if (responseBody.contains("429")) {
                Thread.sleep(500);
                relatedkeywordsAcess(keyword);
            }
        }
        return resultList;
    }

    public HashMap<String, Integer> getSearchCount(String keyword) throws InterruptedException, NoSuchAlgorithmException, InvalidKeyException {
        HashMap<String, Integer> resultMap = new HashMap<>();
        keyword = keyword.replace(" ", "");

        logger.debug("검색량 조회 API : " + "[" + keyword + "]");

        String timestamp = String.valueOf(System.currentTimeMillis());

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_URL + path).newBuilder();
        urlBuilder.addQueryParameter("hintKeywords", keyword);
        urlBuilder.addQueryParameter("showDetail", "1");

        String url = urlBuilder.build().toString();

        String signature = generateSignature(timestamp);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", API_KEY)
                .addHeader("X-CUSTOMER", CUSTOMER_ID)
                .addHeader("X-Timestamp", timestamp)
                .addHeader("X-Signature", signature)
                .build();
        String responseBody = "";
        try (Response response = client.newCall(request).execute()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                responseBody = responseBuilder.toString();

                if (!responseBody.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray keywordList = jsonObject.getJSONArray("keywordList");

                    JSONObject firstKeyword = keywordList.getJSONObject(0);

                    Object monthlyPcQcCntStr = firstKeyword.get("monthlyPcQcCnt");
                    Object monthlyMobileQcCntStr = firstKeyword.get("monthlyMobileQcCnt");

                    int monthlyPcQcCnt = parseKeywordCount(monthlyPcQcCntStr);
                    int monthlyMobileQcCnt = parseKeywordCount(monthlyMobileQcCntStr);
                    int totalSearch = monthlyPcQcCnt + monthlyMobileQcCnt;

                    resultMap.put("monthSearchPc", monthlyPcQcCnt);
                    resultMap.put("monthSearchMobile", monthlyMobileQcCnt);
                    resultMap.put("totalSearch", totalSearch);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            if (responseBody.contains("429")) {
                Thread.sleep(500);
                return getSearchCount(keyword);
            }
        }
        return resultMap;
    }

    private String generateSignature(String timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        String sign = timestamp + "." + "GET" + "." + path;
        SecretKeySpec signingKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(sign.getBytes());
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }

    private int parseKeywordCount(Object value) {
        int returnNumber = 0;
        if (value instanceof Integer) {
            returnNumber = (Integer) value;
        }
        return returnNumber;
    }

}