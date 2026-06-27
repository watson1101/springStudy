package hong.com.user.interfaces.dto;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.user.domain.entity.UserInfo;
import lombok.Data;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private Integer multiDeviceLogin;
    private String token;

    public static UserInfoResponse fromDomain(UserInfo userInfo) {
        UserInfoResponse resp = new UserInfoResponse();
        resp.setId(userInfo.getId());
        resp.setUsername(userInfo.getUsername());
        resp.setNickname(userInfo.getNickname());
        resp.setEmail(userInfo.getEmail());
        resp.setPhone(userInfo.getPhone());
        resp.setAvatar(userInfo.getAvatar());
        resp.setRole(userInfo.getRole() != null ? userInfo.getRole().getCode() : "user");
        resp.setMultiDeviceLogin(userInfo.getMultiDeviceLogin());
        return resp;
    }

    public static UserInfoResponse fromDomainWithToken(UserInfo userInfo) {
        UserInfoResponse resp = fromDomain(userInfo);
        resp.setToken(StpUtil.getTokenValue());
        return resp;
    }
}
