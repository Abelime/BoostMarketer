package camel.BoostMarketer.experience.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Data
@Alias("ExperienceDto")
public class ExperienceDto {
    private Long id;
    private String title; // 체험단 프로젝트명
    private byte[] thumbnail; // 썸네일 이미지 (byte 배열로 저장)
    private String type; // 체험단 유형 (visit, purchase, delivery, press, other)
    private List<String> links; // 등록된 게시글 링크 목록
    private List<String> keywords; // 목표 키워드 목록
}
