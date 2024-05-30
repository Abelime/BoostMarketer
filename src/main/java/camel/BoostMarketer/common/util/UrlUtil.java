package camel.BoostMarketer.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlUtil {
    // URL 인코딩 메서드
    public static String encodeUrl(String url) throws UnsupportedEncodingException {
        String[] parts = url.split("\\?");
        if (parts.length > 1) {
            String baseUrl = parts[0];
            String query = parts[1];
            String[] params = query.split("&");
            StringBuilder encodedQuery = new StringBuilder();

            for (String param : params) {
                int idx = param.indexOf("=");
                String key = param.substring(0, idx);
                String value = param.substring(idx + 1);
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
                if (!encodedQuery.isEmpty()) {
                    encodedQuery.append("&");
                }
                encodedQuery.append(key).append("=").append(encodedValue);
            }

            return baseUrl + "?" + encodedQuery.toString();
        } else {
            return url;
        }
    }
}
