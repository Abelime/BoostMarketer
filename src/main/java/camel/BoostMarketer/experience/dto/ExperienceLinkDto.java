package camel.BoostMarketer.experience.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("ExperienceLinkDto")
public class ExperienceLinkDto {
    private Long experienceId;
    private String link;
    private String postNo;
    private String postTitle;
    private String postDate;
    private String hashtag;
}
