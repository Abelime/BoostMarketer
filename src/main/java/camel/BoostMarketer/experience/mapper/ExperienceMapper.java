package camel.BoostMarketer.experience.mapper;

import camel.BoostMarketer.experience.dto.ExperienceDto;
import camel.BoostMarketer.experience.dto.ExperienceKeywordDto;
import camel.BoostMarketer.experience.dto.ExperienceLinkDto;
import camel.BoostMarketer.experience.dto.ExperienceResponseDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ExperienceMapper {

    void save(@Param("experienceDto") ExperienceDto experienceDto, @Param("email") String email) throws Exception;

    void saveLink(List<ExperienceLinkDto> experienceLinkDtoList) throws Exception;

    void saveKeyword(List<ExperienceKeywordDto> experienceKeywordDtoList) throws Exception;

    int selectExperienceCntInfo(@Param("email") String email, @Param("searchKeyword") String searchKeyword) throws Exception;

    List<ExperienceResponseDto> selectExperienceInfo(@Param("email") String email, RowBounds rowBounds, @Param("searchKeyword") String searchKeyword);
}
