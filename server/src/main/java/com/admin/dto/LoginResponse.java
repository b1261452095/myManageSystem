package com.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录响应DTO
 *
 * @author admin
 */
@Data
@ApiModel(description = "登录响应")
public class LoginResponse {
    
    @ApiModelProperty("访问令牌")
    private String token;
    
    @ApiModelProperty(value = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @ApiModelProperty("过期时间（毫秒）")
    private Long expiresIn;
    
    public LoginResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
