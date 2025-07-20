package com.webmonitor.constant;


public enum ErrorCodeEnum {
  VALIDATE_ERROR(400, "数据校验不通过"),
  BUSINESS_ERROR(400, "业务异常"),
  SYS_ERROR(500, "系统异常，请联系管理员"),
  SYS_PERMISSION(403, "无权限"),
  USER_ALREADY_EXISTS(400, "账号已存在"),
  PASSWORD_DEFINED(400, "账号或密码错误"),
  PASSWORD_NOT_MATCH(400, "两次密码输入不一致"),

  TOKEN_EXPIRED(400, "请重新登录"),

  SQL_ERROR_USER_TASK_TOO_MANY(45000, "目前最多只能有3个任务，请先删除任务"),


  AI_TASK_INTERVAL_TOO_SHORT(400, "任务提醒间隔时间过短"),
  ;


  private Integer code;
  private String msg;

  ErrorCodeEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }


  public static ErrorCodeEnum getError(Integer code) {
    for (ErrorCodeEnum error : ErrorCodeEnum.values()) {
      if (code == error.getCode()) {
        return error;
      }
    }
    return null;
  }
}
