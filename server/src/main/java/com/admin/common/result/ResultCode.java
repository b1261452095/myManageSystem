package com.admin.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 *
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 参数错误
    PARAM_ERROR(400, "参数错误"),
    PARAM_MISSING(400, "参数缺失"),
    PARAM_TYPE_ERROR(400, "参数类型错误"),
    
    // 认证授权
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限访问该资源"),
    TOKEN_INVALID(401, "Token无效"),
    TOKEN_EXPIRED(401, "Token已过期"),
    
    // 用户相关
    USER_NOT_FOUND(404, "用户不存在"),
    USER_DISABLED(403, "用户已被禁用"),
    USERNAME_EXISTS(400, "用户名已存在"),
    EMAIL_EXISTS(400, "邮箱已存在"),
    PASSWORD_ERROR(400, "密码错误"),
    USERNAME_OR_PASSWORD_ERROR(400, "用户名或密码错误"),
    
    // 角色相关
    ROLE_NOT_FOUND(404, "角色不存在"),
    ROLE_CODE_EXISTS(400, "角色编码已存在"),
    ROLE_IN_USE(400, "角色正在使用中，无法删除"),
    
    // 权限相关
    PERMISSION_NOT_FOUND(404, "权限不存在"),
    PERMISSION_CODE_EXISTS(400, "权限编码已存在"),
    
    // 数据相关
    DATA_NOT_FOUND(404, "数据不存在"),
    DATA_EXISTS(400, "数据已存在"),
    DATA_IN_USE(400, "数据正在使用中，无法删除");
    
    private final Integer code;
    private final String message;
}
