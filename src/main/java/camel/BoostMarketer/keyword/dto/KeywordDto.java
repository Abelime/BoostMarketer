package camel.BoostMarketer.keyword.dto;

import camel.BoostMarketer.common.dto.CommonBlogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Alias("KeywordDto")
public class KeywordDto extends CommonBlogDto {
    private Long keywordId;
    private String keywordName; //키워드
    private int categoryId;
    private String categoryName;
    private int rankPc; //순위
    private String rankDate;
    private int monthSearchPc;
    private int monthSearchMobile;
    private int totalSearch;
    private int rankPc5;
    private int rankPc10;
    private int totalSearchExposure;
    private int keywordFix;

}
