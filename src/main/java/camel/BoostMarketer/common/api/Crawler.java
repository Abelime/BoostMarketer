package camel.BoostMarketer.common.api;

import camel.BoostMarketer.blog.dto.RequestBlogDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static camel.BoostMarketer.common.HttpUtil.sendHttpRequest;


public class Crawler {

    private final static ObjectMapper mapper = new ObjectMapper();

    private final static Logger logger = LoggerFactory.getLogger(Crawler.class);

//    public static List<Integer> rankCrawler(RequestBlogDto requestBlogDto) {
//        Instant startTime = Instant.now(); // 시작 시간 기록
//        List<String> keyWordList = requestBlogDto.getKeyWord();
//        List<Integer> rankList = new ArrayList<>();
//
//        // 키워드 리스트를 순회하며 크롤링을 수행합니다.
//        for (String keyword : keyWordList) {
//            try {
//                // 검색어를 UTF-8 형식으로 인코딩합니다.
//                String text = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
//                // URL을 생성합니다.
//                String url = "https://search.naver.com/search.naver?ssc=tab.blog.all&query=" + text;
//
//                // 예외 처리를 추가하여 Jsoup을 사용하여 웹페이지를 가져옵니다.
//                Document doc;
//                doc = Jsoup.connect(url).get();
//                // 블로그 검색 결과에서 링크를 가져옵니다.
//                Elements aTag = doc.select(".title_area a");
//
//                // 링크를 순회하며 블로그 URL과 일치하는지 확인합니다.
//                for (int i = 0; i < aTag.size(); i++) {
//                    String crawlerUrl = aTag.get(i).attr("href");
//                    if (crawlerUrl.equals(requestBlogDto.getBlogUrl())) {
//                        rankList.add(++i);
//                        break;
//                    } else if (i == aTag.size() - 1) { //마지막 까지 일치하지 않으면
//                        rankList.add(0);
//                    }
//                }
//                Thread.sleep(500);
//            } catch (Exception e) {
//                logger.error("Error occurred while fetching keyword ranks for keyword: " + keyword, e);
//                // 예외가 발생한 경우 해당 키워드의 순위를 -1로 설정합니다.
//                rankList.add(-1);
//            }
//
//        }
//        Instant endTime = Instant.now(); // 종료 시간 기록
//        Duration duration = Duration.between(startTime, endTime); // 걸린 시간 계산
//        logger.debug("rankList: {} ", rankList);
//        logger.debug("검색어 순위 계산에 소요된 시간: {} 밀리초", duration.toMillis());
//
//        return rankList;
//    }

    public static List<Integer> rankCrawler(RequestBlogDto requestBlogDto) throws IOException {
        List<Integer> rankList = new ArrayList<>();
        Instant startTime = Instant.now(); // 시작 시간 기록

        // 프록시 서버의 호스트와 포트를 설정합니다.
        String proxyHost = "brd.superproxy.io";
        int proxyPort = 22225; // 프록시 서버 포트

        // System 프로퍼티를 이용하여 전역 프록시 설정을 합니다.
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

        // 병렬로 각 키워드에 대한 순위를 계산합니다.
        requestBlogDto.getKeyWord().parallelStream().forEach(keyword -> {
            try {
                // 검색어를 UTF-8 형식으로 인코딩합니다.
                String text = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

                // URL을 생성합니다.
                String url = "http://search.naver.com/search.naver?ssc=tab.blog.all&query=" + text;

                // Jsoup 연결 객체 생성 및 사용자 에이전트 설정
                Connection conn = Jsoup.connect(url)
                        .referrer("https://www.naver.com/") // Set a referrer
                        .proxy(proxy)
                        .headers(getHeaders()); // Set custom headers


                // 웹페이지를 가져옵니다.
                Document doc = conn.get();

                // 블로그 검색 결과에서 링크를 가져옵니다.
                Elements aTag = doc.select(".title_area a");

                // 링크를 순회하며 블로그 URL과 일치하는지 확인합니다.
                for (int i = 0; i < aTag.size(); i++) {
                    String crawlerUrl = aTag.get(i).attr("href");
                    if (crawlerUrl.equals(requestBlogDto.getBlogUrl())) {
                        rankList.add(i + 1);
                        return; // 순회를 중단하고 람다 표현식을 빠져나갑니다.
                    }
                }
                // 블로그 URL과 일치하는 링크를 찾지 못한 경우
                rankList.add(0);
            } catch (Exception e) {
                logger.error("Error occurred while fetching keyword ranks for keyword: " + keyword, e);
                // 예외가 발생한 경우 해당 키워드의 순위를 -1로 설정합니다.
                rankList.add(-1);
            }
        });

        Instant endTime = Instant.now(); // 종료 시간 기록
        Duration duration = Duration.between(startTime, endTime); // 걸린 시간 계산
        logger.debug("rankList: {} ", rankList);
        logger.debug("검색어 순위 계산에 소요된 시간: {} 밀리초", duration.toMillis());
        return rankList;
    }

    private static Map<String, String> getHeaders() {
        // 인증 정보 설정
        String authUser = "brd-customer-hl_79e5bff5-zone-web_unlocker1";
        String authPassword = "il48wb3obr1n";
        String base64Auth = java.util.Base64.getEncoder().encodeToString((authUser + ":" + authPassword).getBytes());

        Map<String, String> headers = new HashMap<>();
//        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
//        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
//        headers.put("Referer", "https://search.naver.com/search.naver?");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36");
        headers.put("Proxy-Authorization", "Basic " + base64Auth);

        return headers;
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

        String resultStr = sendHttpRequest(url);

        JsonNode postView = mapper.readTree(resultStr);

        if (postView != null) {
            JsonNode postList = postView.get("postList");
            for (JsonNode post : postList) {
                if (post.get("logNo").asText().equals(postNo)) {
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
        String resultStr = sendHttpRequest(url);

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

        String result = sendHttpRequest(url);

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

