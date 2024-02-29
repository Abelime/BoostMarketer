package camel.BoostMarketer.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String sendHttpRequest(String url) {
        logger.debug(" ======================== HTTP Request ======================== ");
        logger.debug(" URL: {}", url);

        StringBuilder result = new StringBuilder();

        try {
            HttpURLConnection connection = setupHttpConnection(url);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

        } catch (Exception e) {
            logger.error("An error occurred while sending HTTP request: {}", e.getMessage());
        }

        return result.toString();
    }

    private static HttpURLConnection setupHttpConnection(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        return connection;
    }
}
