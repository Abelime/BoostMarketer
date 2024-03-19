package camel.BoostMarketer.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@Alias("UserDto")
@EqualsAndHashCode(of= {"email"})
public class UserDto {
    private Long id;                // 회원 번호 (PK)

    @Email
    @NotBlank
    private String email;         // 로그인 ID

    //    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "비밀번호는 대문자, 소문자, 숫자, 특수문자 중 하나 이상을 포함해야 합니다.")
//    @Length(min = 8, max = 15)
    @NotBlank
    private String password;        // 비밀번호

    private String role;

}
