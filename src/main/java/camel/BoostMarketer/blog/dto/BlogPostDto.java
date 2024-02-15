package camel.BoostMarketer.blog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;


@Alias("BlogPostDto")
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogPostDto extends CommonBlogDto{
    //블로그 ID
    //게시글 번호
    private String postTitle; //게시물 제목
    private String postDate; //게시물 날짜
    private String hashtag; //게시물 해시태그
    private int commentCnt; //게시물 해시태그

}
