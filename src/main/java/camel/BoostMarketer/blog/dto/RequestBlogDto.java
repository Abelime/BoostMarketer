package camel.BoostMarketer.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class RequestBlogDto extends CommonBlogDto{
    //블로그 ID
    //게시물 번호
    private String blogUrl; //주소
    private List<String> keyWord;
}

