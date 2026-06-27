package hong.com.user.interfaces;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.common.infrastructure.result.Result;
import hong.com.user.application.UserLoginUseCase;
import hong.com.user.application.UserPasswordUseCase;
import hong.com.user.application.UserRegisterUseCase;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.interfaces.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口层 - REST 控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRegisterUseCase userRegisterUseCase;
    private final UserLoginUseCase userLoginUseCase;
    private final UserPasswordUseCase userPasswordUseCase;

    /** 登录 */
    @PostMapping("/login")
    public Result<UserInfoResponse> login(@Valid @RequestBody LoginRequest request) {
        UserInfo userInfo = userLoginUseCase.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", UserInfoResponse.fromDomainWithToken(userInfo));
    }

    /** 登出 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        userLoginUseCase.logout();
        return Result.success();
    }

    /** 获取当前用户 */
    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser() {
        UserInfo userInfo = userLoginUseCase.getCurrentUser();
        return Result.success(UserInfoResponse.fromDomain(userInfo));
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<UserInfoResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserInfo userInfo = userRegisterUseCase.register(
                request.getUsername(), request.getPassword(),
                request.getNickname(), request.getEmail());
        return Result.success("注册成功", UserInfoResponse.fromDomain(userInfo));
    }

    /** 修改密码 */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userPasswordUseCase.changePassword(StpUtil.getLoginIdAsLong(),
                request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    /** 找回密码 */
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userPasswordUseCase.resetPassword(request.getUsername(), request.getEmail(), request.getNewPassword());
        return Result.success();
    }

    /** 发送验证码 */
    @PostMapping("/send-code")
    public Result<Void> sendResetCode(@RequestParam String email) {
        userPasswordUseCase.sendResetCode(email);
        return Result.success();
    }
}
