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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class NaverSearchAdApi {

    private static final String API_URL = "https://api.searchad.naver.com";
    private static final String API_KEY = "010000000014e06b49cf94f79d7b55664615e46149c913d88dbc28163018dd0398ddb88ccf";
    private static final String SECRET_KEY = "AQAAAAAMIyrKK5R9P8z2k2Ve8M27vi1m88Bz0MhZP21Gg/ZUMg==";
    private static final String CUSTOMER_ID = "3098541";
    private static final String path = "/keywordstool";
    private final static Logger logger = LoggerFactory.getLogger(NaverSearchAdApi.class);

    public static void apiAccess(KeywordDto keywordDto) throws NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
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
//            if (!response.isSuccessful()){
//                throw new RuntimeException("API 요청 실패: " + response);
//            }
            responseBody = response.body().string();

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

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            if (responseBody.contains("429")) {
                Thread.sleep(300);
                apiAccess(keywordDto);
            }
        }
    }

    private static String generateSignature(String timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        String sign = timestamp + "." + "GET" + "." + path;
        SecretKeySpec signingKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(sign.getBytes());
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }

    private static int parseKeywordCount(Object value) {
        int returnNumber = 0;

        if (value instanceof Integer) {
            returnNumber = (Integer) value;
        }

        return returnNumber;
    }

}