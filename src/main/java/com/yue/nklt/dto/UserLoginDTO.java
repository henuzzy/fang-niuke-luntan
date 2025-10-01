package com.yue.nklt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    private Boolean rememberMe = false;
}

