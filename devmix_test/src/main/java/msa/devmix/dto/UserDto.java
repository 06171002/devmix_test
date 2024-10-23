package msa.devmix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import msa.devmix.domain.constant.Location;
import msa.devmix.domain.constant.Role;
import msa.devmix.domain.user.User;

@Getter
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username; //로그인 ID
    private String nickname; //닉네임
    private String email; //이메일
    private String groupName; //소속  e.g.,학교, 회사 등
    private String profileImage; //프로필 이미지

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getGroupName(),
                user.getProfileImage()
        );
    }

    public static UserDto of(Long id, String username, String nickname, String email, String groupName, String profileImage) {
        return new UserDto(id, username, nickname, email, groupName, profileImage);
    }
}
