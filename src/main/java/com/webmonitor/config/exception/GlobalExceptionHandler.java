package com.webmonitor.config.exception;

import com.webmonitor.constant.ErrorCodeEnum;
import com.webmonitor.entity.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  public ResponseVO<String> exception(HttpServletRequest request, MethodArgumentNotValidException exception) {
    BindingResult result = exception.getBindingResult();
    final List<FieldError> fieldErrors = result.getFieldErrors();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < fieldErrors.size(); i++) {
      if (i != fieldErrors.size() - 1) {
        builder.append(fieldErrors.get(i).getDefaultMessage() + ",");
      } else {
        builder.append(fieldErrors.get(i).getDefaultMessage() + ";");
      }
    }
    return ResponseVO.error(ErrorCodeEnum.VALIDATE_ERROR.getCode(), builder.toString());
  }

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