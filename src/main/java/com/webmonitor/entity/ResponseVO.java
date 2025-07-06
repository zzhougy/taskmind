package com.webmonitor.entity;


import lombok.Data;

@Data
public class ResponseVO<T> {
  public static final String MSG = "操作成功";
  private T data;
  private String msg;
  private Integer code;

  public ResponseVO() {
  }

  public ResponseVO(T data, String msg, Integer code) {
    this.data = data;
    this.msg = msg;
    this.code = code;
  }

  public static <T> ResponseVO<T> success(T data) {
    return new ResponseVO<>(data, MSG, 200);
  }

  public static <T> ResponseVO<T> error(Integer code, String msg) {
    return new ResponseVO<>(null, msg, code);
  }
}
