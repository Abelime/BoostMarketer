package camel.BoostMarketer.analysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverContentDto {
    private String title;
    private String date;
    private String author;
    private String url;
    private String type;
    private boolean isInfluencer;
    private int textCount;
    private int imageCount;
    private int videoCount;
    private int linkCount;
    private int visitorCount;
    private int likeCount;
    private int commentCount;


}
