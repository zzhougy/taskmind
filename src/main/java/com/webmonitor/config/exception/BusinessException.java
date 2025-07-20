package com.webmonitor.config.exception;

import com.webmonitor.constant.ErrorCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
  * Description：问题来自用户，该异常问题可以显示给用户看
  */
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


  public BusinessException(ErrorCodeEnum errorCodeEnum) {
    super(errorCodeEnum.getMsg());
    this.code = errorCodeEnum.getCode();
    this.msg = errorCodeEnum.getMsg();
  }


}
