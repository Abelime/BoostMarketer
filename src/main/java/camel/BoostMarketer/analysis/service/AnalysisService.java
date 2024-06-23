package camel.BoostMarketer.analysis.service;

import camel.BoostMarketer.analysis.dto.NaverContentDto;
import camel.BoostMarketer.analysis.dto.RelatedKeywordDto;
import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverAdApi;
import camel.BoostMarketer.common.api.NaverBlogApi;
import camel.BoostMarketer.common.api.NaverTrendsApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final NaverTrendsApi naverTrendsApi;

    private final NaverBlogApi naverBlogApi;

    private final NaverAdApi naverAdApi;

    public List<HashMap<String, Object>> searchTrends(String text, String startDate_Sel, String endDate_Sel) throws Exception {

        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();
        // 전달의 같은 일을 가져오기
        LocalDate sameDayLastMonth = currentDate.minusMonths(1);
        // 원하는 형식으로 날짜를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDate = sameDayLastMonth.format(formatter);    //당월
        String endDate = currentDate.format(formatter);     //당월
        List<HashMap<String, Object>> dataList = naverTrendsApi.apiAccess(text,"date",startDate,endDate);

        if(dataList.isEmpty()){
            return new ArrayList<>();
        }

        double totalRatio = 0;
        int a = -1; //당월 첫날
        int b = -1;

        DecimalFormat  df = new DecimalFormat("#.###");
        String result = "";
        for(HashMap<String, Object> map: dataList){
            result = df.format((double) map.get("ratio"));
            totalRatio += Double.parseDouble(result);
            if(map.get("period").equals(startDate)){
                a = (int) Double.parseDouble(result);
            }
        }

        KeywordDto keywordDto = new KeywordDto();
        keywordDto.setKeywordName(text);
        //당월 검색량 조회
        naverAdApi.apiAccess(keywordDto);
        int monthSearchSum = keywordDto.getMonthSearchPc() + keywordDto.getMonthSearchMobile();

        //ratio 1당 검색량
        double r1 = monthSearchSum/totalRatio;
        r1 = Double.parseDouble(df.format(r1));
        a = (int)(a*r1); //당월 첫날 검색량

        //선택한날 부터 당일까지 ratio
        List<HashMap<String, Object>> dataList_sel = naverTrendsApi.apiAccess(text,"date",startDate_Sel,endDate);
        df = new DecimalFormat("#");

        for(HashMap<String, Object> map: dataList_sel){
            result = df.format(r1*(double)map.get("ratio"));
            map.put("ratio",Integer.parseInt(result));
            if(map.get("period").equals(startDate)){
                b = Integer.parseInt(result);
            }
        }

        //차이(a-b) 보정
        List<HashMap<String, Object>> newDataList = new ArrayList<>();
        for(HashMap<String, Object> map : dataList_sel){
            map.put("ratio",(int)map.get("ratio")+(a-b));
            newDataList.add(map);
            if(map.get("period").equals(endDate_Sel)){
                break;
            }
        }

        return newDataList;
    }

    public List<HashMap<String, Object>> searchTrends2(String text, String startDate, String endDate) throws Exception {

        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();
        // 현재 달의 이전 달로 설정
        LocalDate previousMonth = currentDate.minusMonths(1);
        // 이전 달의 첫,마지막 날짜 구하기
        LocalDate firstDayOfPreviousMonth = previousMonth.withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
        // 형식화된 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //전달의 첫날,마지막날
        String formattedFirstDay = firstDayOfPreviousMonth.format(formatter);
        String formattedDate = lastDayOfPreviousMonth.format(formatter);

        //이전달 ratio
        List<HashMap<String, Object>> trendsList = searchTrends(text,formattedFirstDay,formattedDate);
        if (trendsList.isEmpty()) {
            return new ArrayList<>();
        }

        int trends_sum = 0;
        for(HashMap<String, Object> map : trendsList){
            trends_sum += (int) map.get("ratio");
        }

        //선택한 시작월 1일 부터, 현재를 기준으로 전달의 마지막일까지
        List<HashMap<String, Object>> dataList = naverTrendsApi.apiAccess(text,"month",startDate,formattedDate);

        //ratio 1당 검색량
        double r1 = 0.000;
        DecimalFormat  df = new DecimalFormat("#.###");
        String result = "";
        for(HashMap<String, Object> map : dataList){
            if(map.get("period").equals(formattedFirstDay)){
                result = df.format((double) map.get("ratio"));
                r1 = trends_sum/Double.parseDouble(result);
            }
        }

        List<HashMap<String, Object>> newDataList = new ArrayList<>();
        df = new DecimalFormat("#");
        for(HashMap<String, Object> map : dataList){
            result = df.format(r1*(double)map.get("ratio"));
            map.put("ratio",Integer.parseInt(result));
            newDataList.add(map);
            if(map.get("period").equals(endDate)){
               break;
            }
        }

        return newDataList;
    }

    public Map<String, Object> getAnalyzeData(String keyword) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        KeywordDto keywordDto = new KeywordDto();
        keywordDto.setKeywordName(keyword);

        Map<String, Object> crawlerResult = Crawler.pcSearchCrawler(keyword);
        List<HashMap<String, String>> mobileSectionList = Crawler.mobileSectionSearchCrawler(keyword);

        naverAdApi.apiAccess(keywordDto);

        int totalSearchCnt = keywordDto.getTotalSearch();


        String totalCountApiResult = naverBlogApi.blogTotalCountApi(keyword);
        String monthCountApiResult = naverBlogApi.blogMonthCountApi(keyword);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(totalCountApiResult);
        JsonNode rootNode2 = objectMapper.readTree(monthCountApiResult);

        int totalBlogCnt = rootNode.get("result").get("totalCount").asInt();
        int monthBlogCnt = rootNode2.get("result").get("totalCount").asInt();
        double blogSaturation = (((double) monthBlogCnt / totalSearchCnt) * 100);

        resultMap.put("keywordDto", keywordDto);
        resultMap.put("blogSaturation", blogSaturation);
        resultMap.put("totalBlogCnt", totalBlogCnt);
        resultMap.put("monthBlogCnt", monthBlogCnt);
        resultMap.put("mobileSectionList", mobileSectionList);
        resultMap.put("smartBlockList", crawlerResult.get("smartBlockList"));
        resultMap.put("naverContentDtoList", crawlerResult.get("naverContentDtoList"));
        resultMap.put("smartBlockHrefList", crawlerResult.get("smartBlockHrefList"));
        resultMap.put("pcSectionList", crawlerResult.get("sectionList"));
        resultMap.put("blogList", crawlerResult.get("blogList"));
        return resultMap;
    }

    public List<RelatedKeywordDto> findRelatedKeywords(String keyword) throws Exception {
        List<RelatedKeywordDto> relatedKeywordList = new ArrayList<>();
        Set<String> keywordList = new HashSet<>();
        Set<String> normalizedKeywordSet = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();

        String relatedKeywordListApi = naverBlogApi.relatedKeywordListApi(keyword);
        String autoCompleteKeywordList = naverBlogApi.autoCompleteKeywordListApi(keyword);

        JsonNode relatedKeywordJson = objectMapper.readTree(relatedKeywordListApi).get("result");
        JsonNode rootNode = objectMapper.readTree(autoCompleteKeywordList);

        if (relatedKeywordJson != null) {
            for (JsonNode relatedKeywordNode : relatedKeywordJson) {
                String relatedKeyword = relatedKeywordNode.asText();
                String normalizedRelatedKeyword = normalizeKeyword(relatedKeyword);
                if (!normalizedKeywordSet.contains(normalizedRelatedKeyword)) {
                    keywordList.add(relatedKeyword);
                    normalizedKeywordSet.add(normalizedRelatedKeyword);
                }
            }
        }

        JsonNode itemsArray = rootNode.path("items").get(0);

        if (itemsArray != null) {
            for (JsonNode item : itemsArray) {
                String itemKeyword = item.get(0).asText();
                String normalizedItemKeyword = normalizeKeyword(itemKeyword);
                if (!normalizedItemKeyword.equals(normalizeKeyword(keyword)) && !normalizedKeywordSet.contains(normalizedItemKeyword)) {
                    keywordList.add(itemKeyword);
                    normalizedKeywordSet.add(normalizedItemKeyword);
                }
            }
        }

        if (!keywordList.isEmpty()) {
            for (String keywordName : keywordList) {
                RelatedKeywordDto relatedKeywordDto = new RelatedKeywordDto();

                relatedKeywordDto.setKeywordName(keywordName);

                int totalSearchCnt = 0;

                HashMap<String, Integer> searchCount = naverAdApi.getSearchCount(keywordName);

                if(!searchCount.isEmpty()){
                    relatedKeywordDto.setMonthSearchPc(searchCount.get("monthSearchPc"));
                    relatedKeywordDto.setMonthSearchMobile(searchCount.get("monthSearchMobile"));
                    totalSearchCnt = searchCount.get("totalSearch");
                    relatedKeywordDto.setTotalSearch(totalSearchCnt);
                }

                String totalCountApiResult = naverBlogApi.blogTotalCountApi(keywordName);
                String monthCountApiResult = naverBlogApi.blogMonthCountApi(keywordName);

                JsonNode rootNode1 = objectMapper.readTree(totalCountApiResult);
                JsonNode rootNode2 = objectMapper.readTree(monthCountApiResult);

                int totalBlogCnt = rootNode1.get("result").get("totalCount").asInt();
                int monthBlogCnt = rootNode2.get("result").get("totalCount").asInt();

                double blogSaturation;

                if(totalSearchCnt != 0){
                    blogSaturation = (((double) monthBlogCnt / totalSearchCnt) * 100);
                }else{
                    blogSaturation = 0;
                }

                relatedKeywordDto.setMonthBlogCnt(monthBlogCnt);
                relatedKeywordDto.setTotalBlogCnt(totalBlogCnt);
                relatedKeywordDto.setBlogSaturation(blogSaturation);

                relatedKeywordList.add(relatedKeywordDto);
            }
        }
        return relatedKeywordList;
    }

    public List<NaverContentDto> findNaverContents(String link) throws Exception {
        return Crawler.smartBlockCralwer(link);
    }
    // 키워드를 정규화하는 메서드 (공백 제거 및 소문자로 변환)
    private String normalizeKeyword(String keyword) {
        return keyword.toLowerCase().replaceAll("\\s+", "");
    }

}
