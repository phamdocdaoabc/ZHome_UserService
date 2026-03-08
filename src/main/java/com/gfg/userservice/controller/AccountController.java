package com.gfg.userservice.controller;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.authentication.*;
import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AuthenticationService authenticationService;

    // Đăng ký
    @PostMapping("/register")
    public ApiResponse<String> registerUser(@RequestBody @Valid RegisterDTO registerDTO) {
        authenticationService.register(registerDTO);
        return ApiResponse.<String>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data("Đăng ký thành công. Vui lòng check email để kích hoạt tài khoản!")
                .build();
    }

    @PostMapping("/resend")
    public ApiResponse<String> resendActivationLink(@RequestParam("email") String email) {
        authenticationService.resendActivationLink(email);
        return ApiResponse.<String>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data("Activation link has been sent. Please check your email to activate your account.")
                .build();
    }

    // Xác thực tài khoản
    @GetMapping("/activate")
    public ApiResponse<String> activateAccount(@RequestParam("token") String token) {
        authenticationService.activateAccount(token); // Gọi method trong service để kích hoạt tài khoản
        return ApiResponse.<String>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data("Chúc Mừng Bạn Đã Đăng Ký Tài Khoản Thành Công.")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ApiResponse.<LoginResponse>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data(authenticationService.authenticate(loginDTO))
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponse> refresh(@RequestBody @Valid TokenDTO request) {
        return ApiResponse.<LoginResponse>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data(authenticationService.refreshToken(request))
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestParam("email") String email) {
        authenticationService.forgotPassword(email);
        return ApiResponse.<String>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data("A new password has been sent to your email address.")
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        authenticationService.changePassword(changePasswordDTO);
        return ApiResponse.<String>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data("Password updated successfully")
                .build();
    }

    @PostMapping("/logout") // Đổi tên method cho chuẩn
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    @RequestBody LogoutRequest request) {
        // 1. Cắt chuỗi "Bearer " để lấy token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCodes.INVALID_TOKEN);
        }
        String token = authorizationHeader.substring(7); // Bỏ 7 ký tự đầu "Bearer "
        authenticationService.logout(token);

        if (request.getRefreshToken() != null && !request.getRefreshToken().isEmpty()) {
            authenticationService.logout(request.getRefreshToken());
        }

        return ApiResponse.<Void>builder()
                .message("Logout successful")
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody TokenDTO request) {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .message("Success")
                .traceId(UUID.randomUUID().toString())
                .data(result)
                .build();
    }

}
