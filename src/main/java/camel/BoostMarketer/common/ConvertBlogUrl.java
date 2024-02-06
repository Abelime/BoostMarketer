package camel.BoostMarketer.common;

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
}
