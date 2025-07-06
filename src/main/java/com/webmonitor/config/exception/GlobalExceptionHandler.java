package com.webmonitor.config.exception;

import com.webmonitor.constant.ErrorCodeEnum;
import com.webmonitor.entity.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  @ResponseBody
  public ResponseVO<String> handleBusinessException(BusinessException ex) {
    log.error("BusinessException:", ex);
    // 返回错误信息给客户端
    return ResponseVO.error(ex.getCode(), ex.getMsg());
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public ResponseVO<String> handleRuntimeException(RuntimeException ex) {
    log.error("RuntimeException:", ex);
    // 返回错误信息给客户端
    return ResponseVO.error(ErrorCodeEnum.SYS_ERROR.getCode(), ErrorCodeEnum.SYS_ERROR.getMsg());
  }

}