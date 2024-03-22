package camel.BoostMarketer.common.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("CommonBlogDto")
public class CommonBlogDto {
    private String blogId; //블로그 ID
    private String postNo; //게시글 번호
}
