package camel.BoostMarketer.blog.dto;

import camel.BoostMarketer.common.dto.CommonBlogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;


@EqualsAndHashCode(callSuper = true)
@Data
@Alias("BlogDto")
public class BlogDto extends CommonBlogDto {
    //블로그 ID
    private String blogName; //이름
    private String blogImg; //프로필 이미지
    private String blogTopic; //주제
    private int neighborCnt; //이웃수
    private int visitCnt; //방문수
    private int postCnt; //게시글수
    private String UpdateDt;
    private int topPostsCount; //상위노출 게시글수(블로그탭)
    private int totalSearchPostsCount; //상위노출 게시글수(블로그탭)
}

