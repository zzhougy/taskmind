package com.webmonitor.entity.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginBO {
    @NotBlank(message = "临时授权不能为空")
    private String code;
}
