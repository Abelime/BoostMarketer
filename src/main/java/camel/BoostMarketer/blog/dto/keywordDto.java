package camel.BoostMarketer.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("KeywordDto")
public class keywordDto extends CommonBlogDto{
    //게시글 번호
    private Long keywordId;
    private String keywordName; //키워드
    private int keywordRank; //순위
    private Date rankDate;

    public keywordDto(String postNo, String keywordName, int keywordRank) {
        this.setPostNo(postNo);
        this.keywordName = keywordName;
        this.keywordRank = keywordRank;
    }
}
