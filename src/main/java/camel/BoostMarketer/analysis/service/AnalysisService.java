package camel.BoostMarketer.analysis.service;

import camel.BoostMarketer.common.api.NaverSearchAdApi;
import camel.BoostMarketer.common.api.NaverSearchTrendsApi;
import camel.BoostMarketer.keyword.dto.KeywordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final NaverSearchTrendsApi naverSearchTrendsApi;

    public List<HashMap<String, Object>> searchTrends() throws Exception {

        String text = "가방";
        String startDate = "2024-03-08";    //당월
        String endDate = "2024-04-08";      //당월
        List<HashMap<String, Object>> dataList = naverSearchTrendsApi.apiAccess(text,startDate,endDate);
        double totalRatio = 0;
        for(HashMap<String, Object> map: dataList){
            totalRatio += (double) map.get("ratio");
        }

        KeywordDto keywordDto = new KeywordDto();
        keywordDto.setKeywordName(text);
        //당월 검색량 조회
        NaverSearchAdApi.apiAccess(keywordDto);
        int monthSearchSum = keywordDto.getMonthSearchPc() + keywordDto.getMonthSearchMobile();

        double a = monthSearchSum/totalRatio;

        String startDate_Sel = "2024-02-08";           //선택한날
        String endDate_Sel = "2024-03-08";             //선택한날
        List<HashMap<String, Object>> dataList_sel = naverSearchTrendsApi.apiAccess(text,startDate_Sel,endDate_Sel);    //마지막날은 당일을 넣음
        double totalRatio_Sel = 0;
        List<HashMap<String, Object>> newDataList = new ArrayList<>();
        for(HashMap<String, Object> map: dataList_sel){
            map.put("ratio",a*(double)map.get("ratio"));
            map.put("ratio",a*(double)map.get("ratio"));
            newDataList.add(map);
        }






        return dataList;
    }
}
