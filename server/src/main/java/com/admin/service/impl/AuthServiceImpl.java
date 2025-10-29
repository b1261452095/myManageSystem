package com.admin.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.result.ResultCode;
import com.admin.dto.LoginRequest;
import com.admin.dto.LoginResponse;
import com.admin.entity.User;
import com.admin.service.AuthService;
import com.admin.service.UserService;
import com.admin.util.JwtUtil;
import com.admin.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * @author admin
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        // 设置认证信息到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 获取用户信息
        User user = userService.getUserByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());

        
        return new LoginResponse(token, jwtExpiration);
    }
    
    @Override
    public void logout() {
        // 清除认证信息
        SecurityContextHolder.clearContext();
        
        // 这里可以将token加入黑名单，防止被再次使用
        // 由于使用Redis存储，可以考虑将token存入Redis黑名单
    }
    
    @Override
    public LoginResponse refresh() {
        // 获取当前用户
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 生成新的JWT令牌
        String token = jwtUtil.generateToken(username);
        
        return new LoginResponse(token, jwtExpiration);
    }
}
