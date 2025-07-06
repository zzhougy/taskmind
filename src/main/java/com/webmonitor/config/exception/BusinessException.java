package com.webmonitor.config.exception;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BusinessException extends RuntimeException {

  private Integer code;
  private String msg;

  public BusinessException(Integer code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public BusinessException(String msg) {
    super(msg);
    this.msg = msg;
  }

}
