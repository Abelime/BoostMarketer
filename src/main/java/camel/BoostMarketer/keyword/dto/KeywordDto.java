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
    //게시글 번호
    private Long keywordId;
    private String keywordName; //키워드
    private int keywordRank; //순위
    private String rankDate;

    public KeywordDto(String postNo, String keywordName, int keywordRank) {
        this.setPostNo(postNo);
        this.keywordName = keywordName;
        this.keywordRank = keywordRank;
    }
}
