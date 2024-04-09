package camel.BoostMarketer.analysis.service;

import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.common.api.NaverSearchTrendsApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final NaverSearchTrendsApi naverSearchTrendsApi;

    public List<HashMap<String, Object>> searchTrends(String startDate_Sel, String endDate_Sel) throws Exception {

        String text = "가방";
        String startDate = "2024-03-08";    //당월
        String endDate = "2024-04-08";      //당월
        List<HashMap<String, Object>> dataList = naverSearchTrendsApi.apiAccess(text,"date",startDate,endDate);
        double totalRatio = 0;

        DecimalFormat df = new DecimalFormat("#.###");
        String result = "";
        for(HashMap<String, Object> map: dataList){
            result = df.format((double) map.get("ratio"));
            totalRatio += Double.parseDouble(result);
        }

        KeywordDto keywordDto = new KeywordDto();
        keywordDto.setKeywordName(text);
        //당월 검색량 조회
        NaverSearchAdApi.apiAccess(keywordDto);
        int monthSearchSum = keywordDto.getMonthSearchPc() + keywordDto.getMonthSearchMobile();

        //ratio 1당 검색량
        double a = monthSearchSum/totalRatio;
        a = Double.parseDouble(df.format(a));

        startDate_Sel = startDate_Sel==null?"2024-02-08":startDate_Sel;           //선택한날
        endDate_Sel = endDate_Sel==null?"2024-03-08":endDate_Sel;           //선택한날
        List<HashMap<String, Object>> dataList_sel = naverSearchTrendsApi.apiAccess(text,"date",startDate_Sel,endDate_Sel);
        df = new DecimalFormat("#");
        List<HashMap<String, Object>> newDataList = new ArrayList<>();
        for(HashMap<String, Object> map: dataList_sel){
            map.put("ratio",df.format(a*(double)map.get("ratio")));
            newDataList.add(map);
        }

        return newDataList;
    }

    public List<HashMap<String, Object>> searchTrends2() throws Exception {

        String text = "가방";
        String startDate = "2023-01-01";    //시작
        String endDate = "2024-01-01";      //끝
        List<HashMap<String, Object>> dataList = naverSearchTrendsApi.apiAccess(text,"month",startDate,endDate);

        List<HashMap<String, Object>> newDataList = new ArrayList<>();
        return newDataList;
    }
}
