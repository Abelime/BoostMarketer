package camel.BoostMarketer.common.api;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.blog.dto.RequestBlogDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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

    private final static String proxyHost = "brd.superproxy.io"; //프록시
    private final static int proxyPort = 22225; // 프록시 서버 포트

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


    //해시태그 크롤링
    public static String postTagCrawler(String paramUrl) throws Exception {
        String tagName = "";
        String url = "https://blog.naver.com/BlogTagListInfo.naver?" + paramUrl;
        String resultStr = sendHttpRequest(url);

        JsonNode tag = mapper.readTree(resultStr);

        if (tag.get("taglist").get(0) != null) {
            tagName = URLDecoder.decode(tag.get("taglist").get(0).get("tagName").asText(), StandardCharsets.UTF_8);
        }

        return tagName;
    }

    //방문 숫자 크롤링
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

    //블로그 정보 크롤링
    public static List<BlogDto> blogInfoCrawler(List<String> blogIdList) {
        List<BlogDto> blogDtoList = new ArrayList<>();

        for (String blogId : blogIdList) {
            String blogUrl = "https://blog.naver.com/" + blogId;
            try {
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


                //프로필 이미지 추출
                String imgSrc = mainFrameDoc.select("img[alt=프로필]").attr("src").replace("type=s40", "type=w161");

                // 사용자 이름 추출
                String blogName = mainFrameDoc.select("strong.nick").text();

                BlogDto blogDto = new BlogDto();
                blogDto.setBlogId(blogId);
                blogDto.setBlogName(blogName);
                blogDto.setBlogImg(imgSrc);
                blogDtoList.add(blogDto);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return blogDtoList;
    }

    //전체 글 정보 크롤링
    public static List<BlogPostDto> allPostCrawler(List<String> blogIdList, Map<String, String> map) {
        List<BlogPostDto> postDtoList = new ArrayList<>();

        blogIdList.parallelStream().forEach(blogId -> {
            int page = 1;
          a: for (int i = 1; i <= page; i++) {

                String url = "https://blog.naver.com/PostTitleListAsync.naver?blogId=" + blogId + "&currentPage=" + i + "&countPerPage=30";
                String tagUrl = "https://blog.naver.com/BlogTagListInfo.naver?blogId=" + blogId;

                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(url);
                    String responseBody = client.execute(request, httpResponse ->
                            EntityUtils.toString(httpResponse.getEntity()));

                    JSONObject jsonObject = new JSONObject(responseBody);

                    int totalCount = jsonObject.getInt("totalCount");
                    String tagQueryString = jsonObject.getString("tagQueryString");
                    tagUrl += tagQueryString;

                    HttpGet tagRequest = new HttpGet(tagUrl);
                    String tagResponseBody = client.execute(tagRequest, httpResponse ->
                            EntityUtils.toString(httpResponse.getEntity()));

                    JSONObject tagJsonObject = new JSONObject(tagResponseBody);
                    JSONArray tagList = tagJsonObject.getJSONArray("taglist");

                    Map<String, String> tagMap = new HashMap<>();
                    for (int x = 0; x < tagList.length(); x++) {
                        JSONObject tag = tagList.getJSONObject(x);
                        String logNo = tag.getString("logno");
                        String tagName = URLDecoder.decode(tag.getString("tagName"), StandardCharsets.UTF_8);
                        tagMap.put(logNo, tagName);
                    }

                    if (totalCount > 30) {
                        page = totalCount / 30;
                        if (totalCount % 30 != 0) {
                            page = (totalCount / 30) + 1;
                        }
                    }

                    JSONArray postList = jsonObject.getJSONArray("postList");

                    for (int y = 0; y < postList.length(); y++) {
                        BlogPostDto blogPostDto = new BlogPostDto();

                        JSONObject post = postList.getJSONObject(y);
                        String logNo = post.getString("logNo");

                        if (map != null && map.get(blogId).equals(logNo)) {
                            break a;
                        }

                        String title = URLDecoder.decode(post.getString("title"), StandardCharsets.UTF_8);
                        String addDate = post.getString("addDate");
                        String tagName = tagMap.get(logNo);

                        blogPostDto.setBlogId(blogId);
                        blogPostDto.setPostNo(logNo);
                        blogPostDto.setPostDate(addDate);
                        blogPostDto.setPostTitle(title);
                        blogPostDto.setHashtag(tagName);

                        postDtoList.add(blogPostDto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return postDtoList;
    }

}

