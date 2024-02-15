package camel.BoostMarketer.common.api;

import camel.BoostMarketer.blog.dto.RequestBlogDto;
import camel.BoostMarketer.common.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Crawler {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static Logger logger = LoggerFactory.getLogger(Crawler.class);

    //블로그 순위 크롤링
    public static List<Integer> rankCrawler(RequestBlogDto requestBlogDto) {
        List<String> keyWordList = requestBlogDto.getKeyWord();
        List<Integer> rankList = new ArrayList<>();

        // 순위와 크롤러 URL을 초기화합니다.
        int rank = 0;
        String crawlerUrl = "";

        // 키워드 리스트를 순회하며 크롤링을 수행합니다.
        for (String keyword : keyWordList) {
            // 검색어를 UTF-8 형식으로 인코딩합니다.
            String text = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            // URL을 생성합니다.
            String url = "https://search.naver.com/search.naver?ssc=tab.blog.all&query=" + text;

            // 예외 처리를 추가하여 Jsoup을 사용하여 웹페이지를 가져옵니다.
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while connecting to the URL: " + url, e);
            }

            // 블로그 검색 결과에서 링크를 가져옵니다.
            Elements aTag = doc.select(".title_area a");

            // 링크를 순회하며 블로그 URL과 일치하는지 확인합니다.
            for (Element title : aTag) {
                rank++;
                crawlerUrl = title.attr("href");

                // 블로그 URL과 일치하면 반복문을 종료합니다.
                if (crawlerUrl.equals(requestBlogDto.getBlogUrl())) break;
            }

            // 순위가 30위인데 블로그 URL이 일치하지 않는 경우 -1을 반환합니다.
            if (rank == 30 && !crawlerUrl.equals(requestBlogDto.getBlogUrl())) {
                rank = -1;
            }

            rankList.add(rank);

            // 순위와 크롤러 URL을 초기화합니다.
            crawlerUrl = "";
            rank = 0;
        }

        // 마지막에 순위를 반환합니다. (마지막 키워드의 순위만 반환됩니다)
        return rankList;
    }

    //블로그 정보 및 게시글 크롤링(jsoup)
    public static int blogInfoCrawler(RequestBlogDto requestBlogDto) throws IOException {
        String blogUrl = requestBlogDto.getBlogUrl();

        // 해당 URL에서 HTML을 가져옴
        Document doc = Jsoup.connect(blogUrl).get();

        // mainFrame의 src 속성 값 가져오기
        Element mainFrame = doc.selectFirst("iframe[name=mainFrame]");
        String mainFrameSrc = mainFrame.attr("src");

        // 절대 URL로 변환
        URL absoluteUrl = new URL(new URL(blogUrl), mainFrameSrc);
        String mainFrameAbsoluteUrl = absoluteUrl.toString();

        // mainFrame의 HTML 가져오기
        Document mainFrameDoc = Jsoup.connect(mainFrameAbsoluteUrl).get();

        // 요소 선택 및 출력
//        printElementText(mainFrameDoc, ".se-module.se-module-text.se-title-text", "postTitle");
//        printElementText(mainFrameDoc, ".link.pcol2", "글쓴이");
//        printElementText(mainFrameDoc, ".se_publishDate.pcol2", "postDate");
//        printElementText(mainFrameDoc, ".itemtitlefont", "blogTitle");
//        printElementText(mainFrameDoc, "._commentCount", "commentCount");

        return 0;
    }

    //개시물 정보 크롤링
    public static Map<String, Object> postInfoCrawler(String paramUrl, String postNo) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String url = "https://blog.naver.com/PostViewBottomTitleListAsync.naver?" + paramUrl;

        String resultStr = HttpUtil.sendHttpRequest(url);

        JsonNode postView = mapper.readTree(resultStr);

        if (postView != null) {
            JsonNode postList = postView.get("postList");
            for (JsonNode post : postList) {
                if(post.get("logNo").asText().equals(postNo)){
                    String postTitle = URLDecoder.decode(post.get("filteredEncodedTitle").asText(), StandardCharsets.UTF_8);
                    String postDate = post.get("addDate").asText();
                    int commentCnt = post.get("commentCount").asInt();

                    resultMap.put("postTitle", postTitle);
                    resultMap.put("postDate", postDate);
                    resultMap.put("commentCnt", commentCnt);

                    break;
                }
            }
        }

        String hashtag = postTagCrawler(paramUrl);
        resultMap.put("hashtag", hashtag);

        return resultMap;
    }


    //블로그 글 해시태그 크롤링
    public static String postTagCrawler(String paramUrl) throws Exception {
        String tagName = "";
        String url = "https://blog.naver.com/BlogTagListInfo.naver?" + paramUrl;
        String resultStr = HttpUtil.sendHttpRequest(url);

        JsonNode tag = mapper.readTree(resultStr);

        if (tag != null) {
            tagName = URLDecoder.decode(tag.get("taglist").get(0).get("tagName").asText(), StandardCharsets.UTF_8);
            System.out.println("tagName = " + tagName);
        }

        return tagName;
    }

    //블로그 방문 숫자 크롤링
    public static int visitorCntCrawler(String blogId) throws Exception {
        String url = "https://blog.naver.com/NVisitorgp4Ajax.naver?blogId=" + blogId;

        String result = HttpUtil.sendHttpRequest(url);

        // XML을 JsonNode로 변환
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode xmlJSONObj = xmlMapper.readTree(result);
        JsonNode jsonNode = xmlJSONObj.get("visitorcnt");

        int totalCnt = 0;
        for (JsonNode visitor : jsonNode) {
            int cnt = visitor.get("cnt").asInt();
            totalCnt += cnt;
        }


        return totalCnt;
    }

    public void printElementText(Document doc, String selector, String label) {
        Elements elements = doc.select(selector);
        if (!elements.isEmpty()) {
            System.out.println(label + ": " + elements.text());
        } else {
            System.out.println(label + "를 찾을 수 없습니다.");
        }
    }


}

