package camel.BoostMarketer.experience.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("ExperienceResponseDto")
public class ExperienceResponseDto {
    private Long id;
    private String title; // 체험단 프로젝트명
    private byte[] thumbnail; // 썸네일 이미지 (byte 배열로 저장)
    private String thumbnailBase64;
    private String type; // 체험단 유형 (visit, purchase, delivery, press, other)
    private int linkCount;
    private int keywordCount;
    private String createdAt;
}
