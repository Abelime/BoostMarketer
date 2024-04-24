package camel.BoostMarketer.analysis.service;

import camel.BoostMarketer.common.api.Crawler;
import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.common.api.NaverSearchTrendsApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final NaverSearchTrendsApi naverSearchTrendsApi;

    public List<HashMap<String, Object>> searchTrends(String text, String startDate_Sel, String endDate_Sel) throws Exception {

        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();
        // 전달의 같은 일을 가져오기
        LocalDate sameDayLastMonth = currentDate.minusMonths(1);
        // 원하는 형식으로 날짜를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDate = sameDayLastMonth.format(formatter);    //당월
        String endDate = currentDate.format(formatter);     //당월
        List<HashMap<String, Object>> dataList = naverSearchTrendsApi.apiAccess(text,"date",startDate,endDate);
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
        NaverSearchAdApi.apiAccess(keywordDto);
        int monthSearchSum = keywordDto.getMonthSearchPc() + keywordDto.getMonthSearchMobile();

        //ratio 1당 검색량
        double r1 = monthSearchSum/totalRatio;
        r1 = Double.parseDouble(df.format(r1));
        a = (int)(a*r1); //당월 첫날 검색량

        //선택한날 부터 당일까지 ratio
        List<HashMap<String, Object>> dataList_sel = naverSearchTrendsApi.apiAccess(text,"date",startDate_Sel,endDate);
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
        int trends_sum = 0;
        for(HashMap<String, Object> map : trendsList){
            trends_sum += (int) map.get("ratio");
        }

//        // 첫 번째 날짜의 마지막 날짜 계산
//        LocalDate startDate1 = LocalDate.of(Integer.parseInt(startYear), Integer.parseInt(startMonth), 1);
//        LocalDate endDate1 = startDate1.withDayOfMonth(startDate1.lengthOfMonth());
//        // 두 번째 날짜의 마지막 날짜 계산
//        LocalDate startDate2 = LocalDate.of(Integer.parseInt(endYear), Integer.parseInt(endMonth), 1);
//        LocalDate endDate2 = startDate2.withDayOfMonth(startDate2.lengthOfMonth());
//
//        String startDate = startDate1.format(formatter);    //선택한 시작월의 1일
//        String endDate = startDate2.format(formatter);      //선택한 끝월의 1일
        //선택한 시작월 1일 부터, 현재를 기준으로 전달의 마지막일까지
        List<HashMap<String, Object>> dataList = naverSearchTrendsApi.apiAccess(text,"month",startDate,formattedDate);

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

    public Map<String, Object> getAnalyzeDate(String keyword) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Map<String, List<HashMap<String, String>>> crawlerResult = Crawler.sectionSearchCrawler(keyword);

        List<KeywordDto> relatedkeywordList = NaverSearchAdApi.relatedkeywordsAcess(keyword);

        if(!relatedkeywordList.isEmpty()){
            resultMap.put("keywordDto", relatedkeywordList.get(0));
            relatedkeywordList.remove(0);
        }

        resultMap.put("sectionList", crawlerResult.get("sectionList"));
        resultMap.put("blogList", crawlerResult.get("blogList"));
        resultMap.put("relatedkeywordList", relatedkeywordList);
        return resultMap;
    }
}
