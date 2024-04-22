package camel.BoostMarketer.common.api;

import camel.BoostMarketer.blog.dto.BlogDto;
import camel.BoostMarketer.blog.dto.BlogPostDto;
import camel.BoostMarketer.common.util.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import generator.RandomUserAgentGenerator;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static camel.BoostMarketer.common.util.HttpUtil.sendHttpRequest;


public class Crawler {

    private final static Logger logger = LoggerFactory.getLogger(Crawler.class);

    public static Map<String, String> headerData = new HashMap<>();

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

    //블로그 정보 크롤링 (jsoup)
    public static BlogDto blogInfoCrawler(String blogId) throws Exception {
        BlogDto blogDto = new BlogDto();

        String blogUrl = "https://blog.naver.com/" + blogId;

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

        blogDto.setBlogId(blogId);
        blogDto.setBlogName(blogName);
        blogDto.setBlogImg(imgSrc);

        return blogDto;
    }

    //전체 글 정보 크롤링
    public static List<BlogPostDto> allPostCrawler(List<String> blogIdList, Map<String, String> map) {
        List<BlogPostDto> postDtoList = new ArrayList<>();

        blogIdList.parallelStream().forEach(blogId -> {
            int page = 1;
            a:
            for (int i = 1; i <= page; i++) {

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
                        title = HtmlUtils.htmlUnescape(title);

                        String addDate = post.getString("addDate");
                        Date date = DateUtil.convertToLocalDate(addDate);
                        addDate = DateUtil.formatDate(date);

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

    //블로그 탭 순위 크롤링(Jsoup)
    public static Map<String, Integer> blogTabCrawler(List<String> blogIds, String keyword) throws Exception {
        logger.info("블로그탭 크롤링 : " + "[" + keyword + "]");
        Map<String, Integer> resultMap = new HashMap<>();

        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String url = "https://search.naver.com/search.naver?ssc=tab.blog.all&sm=tab_jum&query=" + encodedKeyword;
        String referrer = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=" + encodedKeyword;


        Document document = Jsoup.connect(url)
                .referrer(referrer)
                .headers(headerData)
                .get();

        Elements aTags = document.select(".title_area a");

        for (Element aTag : aTags) {
            String crawlerUrl = aTag.attr("href");
            crawlerUrl = crawlerUrl.replace("m.blog", "blog");
            for (String blogId : blogIds) {
                if (crawlerUrl.contains("/" + blogId + "/")) {
                    resultMap.put(crawlerUrl, aTags.indexOf(aTag) + 1);
                }
            }
            if (aTags.indexOf(aTag) == 9) break; //10등까지 순위 확인
        }

        return resultMap;
    }

    //통합검색 노출 확인(Jsoup)
    public static List<String> totalSearchCrawler(List<String> blogIds, String keyword) throws Exception {
        logger.info("통합검색 크롤링 : " + "[" + keyword + "]");
        List<String> result = new ArrayList<>();

        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=" + encodedKeyword;
        String referrer = "https://www.naver.com/";

        Document document = Jsoup.connect(url)
                .referrer(referrer)
                .headers(headerData)
                .get();

        Set<String> set = extractHrefValues(document);

        for (String blogUrl : set) {
            for (String blogId : blogIds) {
                if (blogUrl.contains("/" + blogId + "/")) {
                    blogUrl = blogUrl.replace("m.blog", "blog");
                    result.add(blogUrl);
                }
            }
        }

        return result;
    }

    //통합검색 블로그URL 태그
    private static Set<String> extractHrefValues(Document document) {
        Set<String> hrefValues = new HashSet<>();

        String[] selectors = new String[]{
                ".title_link", ".link_tit",
                ".fds-comps-right-image-text-content",
                ".fds-comps-right-image-text-title-wrap"
        };

        for (String selector : selectors) {
            Elements selectedElements = document.select(selector);

            selectedElements.forEach(element -> {
                String hrefValue = element.attr("href");
                if (hrefValue.startsWith("https://blog.naver.com/")) {
                    hrefValues.add(hrefValue);
                }
            });
        }

        return hrefValues;
    }

    public static List<HashMap<String, String>> sectionSearchCrawler(String keyword) {
        List<HashMap<String, String>> result = new ArrayList<>();
        keyword = keyword==null?"가방":keyword;
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=" + encodedKeyword + " &oquery="  + encodedKeyword + "tqi=imk3isqo1LwssDUBj5VssssstYC-334687";
            String referrer = "https://www.naver.com/";

            Document document = Jsoup.connect(url)
                    .referrer(referrer)
                    .headers(headerData)
                    .get();

            // style="display: none;"이 포함된 태그와 그 하위 태그 제거
            Elements elementsToRemove = document.select("[style*='display: none']");
            elementsToRemove.remove();


            Elements elements = document.select("div.api_subject_bx");
            aa :for (Element element : elements) {
                Elements elements2 = element.select("h2:not([style*='display: none']), span.fds-comps-header-headline:not([style*='display: none']),section.sc_new.sp_ntotal,section.sc_new.sp_intent_fashion,ul.lst_total,ico_kin_snippet");
                for (Element element2 : elements2) {
                    HashMap<String, String> map = new HashMap<>();
                    if(element2.text().equals("연관 검색어")){
                        break aa;
                    }else if (!elements2.select("div.bx_snippet").isEmpty()) {
                        map.put("section","지식스니펫");
                    }else if (!elements2.select("ul.lst_total").isEmpty()) {
                        map.put("section","웹사이트");
                    } else {
                        map.put("section",element2.text());
                    }
                    element.select("div.snippet_rel_wrap").remove();
                    Elements elements3 = element.select("li.lst,li.box,li.bx,a.fds-comps-keyword-chip,div.fds-ugc-block-mod,a.fashion_card,a.product");
                    map.put("cnt",String.valueOf(elements3.size()));
                    result.add(map);
                }
            }



        } catch (Exception e) {
            logger.error("Error occurred while fetching total_search for keyword: " + keyword, e);
        }

        return result;
    }

    public static List<HashMap<String, String>> blogtabPostingCrawler(String keyword) {
        List<HashMap<String, String>> result = new ArrayList<>();
        keyword = keyword==null?"가방":keyword;
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=" + encodedKeyword;
            String referrer = "https://www.naver.com/";

            Document document = Jsoup.connect(url)
                    .referrer(referrer)
                    .headers(headerData)
                    .get();

            // style="display: none;"이 포함된 태그와 그 하위 태그 제거
            Elements elementsToRemove = document.select("[style*='display: none']");
            elementsToRemove.remove();

            Elements elements = document.select("div.fds-ugc-block-mod-list");

            for(Element element : elements){
                Elements elements2 = element.select("div.fds-ugc-block-mod");
                for(Element element2 : elements2){
                    HashMap<String, String> map = new HashMap<>();
                    Elements blog = element2.select("a.fds-info-inner-text");
                    map.put("blogUrl",blog.attr("href"));
                    map.put("blogName",blog.select("span").text());
                    String regex = "(\\d+일 전)|(\\d+주 전)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(element2.select("span.fds-info-sub-inner-text").text());
                    if (matcher.find()) {
                        map.put("glTime",matcher.group());
                    }else {
                        map.put("glTime",element2.select("span.fds-info-sub-inner-text").text());
                    }

                    Elements gl = element2.select("a.fds-comps-right-image-text-title,a.fds-comps-right-image-text-title-wrap");
                    map.put("glUrl",gl.attr("href"));
                    map.put("glName",gl.select("span").text());
                    result.add(map);
                }
            }


        }catch (Exception e) {
            logger.error("Error occurred while fetching total_search for keyword: " + keyword, e);
        }
        return result;
    }

//    public static Map<String, String> getHeaders() {
//        headerData.put("User-Agent", RandomUserAgentGenerator.getNextNonMobile());
//        return headerData;
//    }

    public static void sleep(String reason) {
        try {
            if(reason.equals("Exception")){
                Thread.sleep(60000);
            }else if(reason.equals("random")){
                Random random = new Random();
                int sleepTime = 400 + random.nextInt(200);
                Thread.sleep(sleepTime);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void updateHeaderData(int attempts) {
        Map<String, String> headerData = Crawler.headerData;
        if (attempts == 0) {
            headerData.put("User-Agent", RandomUserAgentGenerator.getNextNonMobile());
        } else if (attempts == 1) {
            headerData.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        } else if (attempts == 2) {
            headerData.put("Accept-Encoding", "gzip, deflate, br, zstd");
        } else if (attempts == 3) {
            headerData.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        }
    }
}
