package camel.BoostMarketer.analysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlogAnalysisDto {
    private int minVisitorCount;
    private int maxVisitorCount;
    private int minImageCount;
    private int maxImageCount;
    private int minVideoCount;
    private int maxVideoCount;
    private int minTextCount;
    private int maxTextCount;
}
