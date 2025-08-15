package com.webmonitor.config.exception;

import com.webmonitor.constant.ErrorCodeEnum;
import lombok.Getter;
import lombok.Setter;


/**
 * Description：问题来自平台系统，该异常问题不能显示给用户看
 */
@Getter
@Setter
public class SystemException extends RuntimeException {

  private Integer code;
  private String msg;

  public SystemException(Integer code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public SystemException(String msg) {
    super(msg);
    this.msg = msg;
  }

  public SystemException(ErrorCodeEnum errorCodeEnum) {
    super(errorCodeEnum.getMsg());
    this.code = errorCodeEnum.getCode();
    this.msg = errorCodeEnum.getMsg();
  }

}
