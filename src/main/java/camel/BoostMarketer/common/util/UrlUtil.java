package camel.BoostMarketer.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static List<String> smartBlockMainTitle(String data) {
        List<String> smartBlockTitle = new ArrayList<>();

        // 정규 표현식 패턴을 정의합니다.
        Pattern pattern = Pattern.compile("\"text\":\\{\"content\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            smartBlockTitle.add(matcher.group(1));
        }

        return smartBlockTitle;
    }

    public static List<String> smartBlockAPiUrl(String data) {
        List<String> apiUrl = new ArrayList<>();

        // 정규 표현식 패턴을 정의합니다.
        String hrefPattern = "\"data-lb-trigger\":\"(.*?)\"";
        Pattern pattern = Pattern.compile(hrefPattern);
        Matcher matcher = pattern.matcher(data);

        // 매칭된 href 값을 추출하여 리스트에 추가합니다.
        while (matcher.find()) {
            apiUrl.add(matcher.group(1));
        }

        return apiUrl;
    }

    public static List<String> smartBlockTitleRegex(String data) {
        List<String> title = new ArrayList<>();

        // 정규 표현식 패턴을 정의합니다.
        String regex = "\"fds-comps-right-image-text-title\".*?\"html\":\"(.*?)\"}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        // 매칭된 data-url 값을 추출하여 리스트에 추가합니다.
        while (matcher.find()) {
            title.add(matcher.group(1).replace("<mark>", "").replace("</mark>",""));
        }

        return title;
    }

    public static List<String> smartBlockAuthorRegex(String data) {
        List<String> author = new ArrayList<>();

        // 정규 표현식 패턴을 정의합니다.
        String regex = "\"className\":\"fds-info-inner-text\",\"children\":\\[\\{\"component\":\"Text\",\"props\":\\{\"content\":\"(.*?)\"}"; // 정규식 패턴 설정
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        // 매칭된 data-url 값을 추출하여 리스트에 추가합니다.
        while (matcher.find()) {
            author.add(matcher.group(1));
        }

        return author;
    }

    public static List<String> smartBlockDateRegex(String data) {
        List<String> date = new ArrayList<>();

        // 정규 표현식 패턴을 정의합니다.
        String regex = "fds-info-sub-inner-text\",\"content\":\"(.*?)\"}"; // 정규식 패턴 설정
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        // 매칭된 data-url 값을 추출하여 리스트에 추가합니다.
        while (matcher.find()) {
            date.add(matcher.group(1));
        }

        return date;
    }

    public static List<String> smartBlockHref(String data) {
        List<String> apiUrl = new ArrayList<>();

        // 정규 표현식 패턴을 정의합니다.
        String hrefPattern = "\"data-url\":\"(.*?)\"";
        Pattern pattern = Pattern.compile(hrefPattern);
        Matcher matcher = pattern.matcher(data);

        // 매칭된 data-url 값을 추출하여 리스트에 추가합니다.
        while (matcher.find()) {
            apiUrl.add(matcher.group(1));
        }

        return apiUrl;
    }

}
