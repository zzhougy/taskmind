package com.webmonitor.entity.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsernamePasswordLoginBO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}