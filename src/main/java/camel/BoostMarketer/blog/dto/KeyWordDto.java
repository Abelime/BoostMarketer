package camel.BoostMarketer.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
//@Alias("BlogDto")
public class KeyWordDto extends CommonBlogDto{
    //게시글 번호
    private List<String> KeyWordName; //키워드
    private List<Integer> rank; //순위

}
