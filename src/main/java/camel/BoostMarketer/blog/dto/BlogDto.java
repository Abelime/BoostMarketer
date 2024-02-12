package camel.BoostMarketer.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BlogDto extends CommonBlogDto{
    //블로그 ID
    private String blogName; //이름
    private String blogTopic; //주제
    private int neighborCnt; //이웃수
    private int visitCnt; //방문수
}

