package com.admin.controller;

import com.admin.common.result.Result;
import com.admin.dto.LoginRequest;
import com.admin.dto.LoginResponse;
import com.admin.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author admin
 */
@Api(tags = "认证管理", description = "用户登录、注销等认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return Result.success(response);
    }
    
    /**
     * 用户注销
     */
    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
    
    /**
     * 刷新Token
     */
    @ApiOperation("刷新Token")
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh() {
        LoginResponse response = authService.refresh();
        return Result.success(response);
    }
}
