package camel.BoostMarketer.common;

import java.util.HashMap;
import java.util.List;
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

    public static String getRankForKeywordDate(List<Map<String, Object>> rankDates, String date) {
        for (Map<String, Object> rankDate : rankDates) {
            if (date.equals(rankDate.get("date"))) {
                Object rank = rankDate.get("rank");
                // rank 값이 0이면 "미확인"을, null이면 "-"를 반환합니다.
                if (rank == null || rank.equals("")) {
                    return "-";
                } else {
                    int rankInt = Integer.parseInt(String.valueOf(rank));
                    return rankInt == 0 ? "미확인" : String.valueOf(rank);
                }
            }
        }
        // 날짜에 해당하는 rank가 없으면 "-"을 반환합니다.
        return "-";
    }
}
