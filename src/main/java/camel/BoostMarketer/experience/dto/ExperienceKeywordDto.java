package camel.BoostMarketer.experience.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("ExperienceKeywordDto")
public class ExperienceKeywordDto {
    private Long experienceId;
    private Long keywordId;
}
