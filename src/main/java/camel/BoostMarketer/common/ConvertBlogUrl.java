package camel.BoostMarketer.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertBlogUrl {
    public static Map<String, String> convertUrl(String blogUrl) {
        Map<String, String> blogInfo = new HashMap<>();

        // 정규표현식 패턴
        String pattern = "https://blog.naver.com/(\\w+)/(\\d+)";
        Pattern r = Pattern.compile(pattern);

        // Matcher 객체 생성
        Matcher m = r.matcher(blogUrl);

        // 매칭된 경우 값 추출
        if (m.find()) {
            blogInfo.put("blogId", m.group(1));
            blogInfo.put("postNo", m.group(2));
        }

        return blogInfo;
    }

    public static String urlToPostNo(String blogUrl) {
        // 정규표현식 패턴
        String pattern = "https://blog.naver.com/(\\w+)/(\\d+)";
        Pattern r = Pattern.compile(pattern);

        // Matcher 객체 생성
        Matcher m = r.matcher(blogUrl);
        String postNo = "";
        // 매칭된 경우 값 추출
        if (m.find()) {
            postNo = m.group(2);
        }

        return postNo;
    }

    public static String urlToBlogId(String blogUrl) {
        // 정규표현식 패턴
        String pattern = "https://blog.naver.com/(\\w+)/(\\d+)";
        Pattern r = Pattern.compile(pattern);

        // Matcher 객체 생성
        Matcher m = r.matcher(blogUrl);
        String blogId = "";
        // 매칭된 경우 값 추출
        if (m.find()) {
            blogId = m.group(1);
        }

        return blogId;
    }
    
    public static String extractBlogId(String urlString) throws Exception {
        URL url = new URL(urlString);
        String query = url.getQuery();

        if (query != null) {
            Pattern pattern = Pattern.compile("blogId=([^&]*)");
            Matcher matcher = pattern.matcher(query);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        throw new IllegalArgumentException("Blog ID not found in URL");
    }
}
