package camel.BoostMarketer.blog.api;

import camel.BoostMarketer.blog.dto.BlogDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CrawlerRank {
    public int getRank(BlogDto blogDto){
        String text = blogDto.getKeyWord();
        text = URLEncoder.encode(text, StandardCharsets.UTF_8);

        String url = "https://search.naver.com/search.naver?ssc=tab.blog.all&query=" + text; // 블로그
//        String url = "https://s.search.naver.com/p/review/47/search.naver?ssc=tab.itb.all&api_type=0&query=" + text; 인기글
//        String url = "https://s.search.naver.com/p/intentblock/34/search.naver?bid=SYS-0000000035467638&query=" + text + "&where=nx_bridge" //인기글 주제
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements aTag = doc.select(".title_area a");

        int rank = 0;
        String crawlerUrl = "";

        for (Element title : aTag) {
            rank++;
//            System.out.println(idx);
//            System.out.println(title.text()); 글 제목
            crawlerUrl = title.attr("href");

            if(crawlerUrl.equals(blogDto.getBlogUrl())) break;
        }

        System.out.println("순위 : " + rank);

        return rank;
    }
}

