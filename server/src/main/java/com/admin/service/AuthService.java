package com.admin.service;

import com.admin.dto.LoginRequest;
import com.admin.dto.LoginResponse;

/**
 * 认证服务接口
 *
 * @author admin
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 用户注销
     */
    void logout();
    
    /**
     * 刷新Token
     */
    LoginResponse refresh();
}
