package camel.BoostMarketer.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String sendHttpRequest(String url) throws Exception {
        logger.debug(" ============================================================ ");
        logger.debug(" ======================== 알 림 ========================  ");
        logger.debug(" ============================================================ ");
        logger.debug(" url: {}", url);

        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL openUrl = new URL(url);
            connection = (HttpURLConnection) openUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            logger.debug("■ sendHttpRequest result: {}", result);

        } catch (Exception e) {
            logger.error("An error occurred while sending HTTP request: {}", e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.error("An error occurred while closing reader: {}", e.getMessage());
                }
            }
        }

        return result.toString();
    }
}
