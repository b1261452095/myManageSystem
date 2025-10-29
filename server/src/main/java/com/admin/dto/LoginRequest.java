package com.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author admin
 */
@Data
@ApiModel(description = "登录请求")
public class LoginRequest {
    
    @ApiModelProperty(value = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @ApiModelProperty(value = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
