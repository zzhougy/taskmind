package com.webmonitor.entity.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginVO {
  private String token;
}
