package camel.BoostMarketer.analysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RelatedKeywordDto {
    private String keywordName;
    private int monthSearchPc;
    private int monthSearchMobile;
    private int totalSearch;
    private int monthBlogCnt;
    private int totalBlogCnt;
    private Double blogSaturation;


}
