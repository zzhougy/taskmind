package com.webmonitor.controller;

import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.LoginBO;
import com.webmonitor.entity.bo.RegisterBO;
import com.webmonitor.entity.bo.UsernamePasswordLoginBO;
import com.webmonitor.entity.vo.LoginVO;
import com.webmonitor.entity.vo.UserVO;
import com.webmonitor.service.UserService;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.constant.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  // todo
  @GuestAccess
  @PostMapping("/wx/login")
  public ResponseVO<LoginVO> login(@Valid @RequestBody LoginBO form) {
    return ResponseVO.success(userService.login(form.getCode()));
  }


  // todo
  @GuestAccess
  @PostMapping("/login")
  public ResponseVO<LoginVO> loginByUsernamePassword(@Valid @RequestBody UsernamePasswordLoginBO form) {
    return ResponseVO.success(userService.loginByUsernamePassword(form.getUsername(), form.getPassword()));
  }

  // todo
  @GuestAccess
  @PostMapping("/register")
  public ResponseVO<Boolean> register(@Valid @RequestBody RegisterBO form) {
    if (!form.getPassword().equals(form.getConfirmPassword())) {
      throw new BusinessException(ErrorCodeEnum.PASSWORD_NOT_MATCH.getCode(), ErrorCodeEnum.PASSWORD_NOT_MATCH.getMsg());
    }
    return ResponseVO.success(userService.register(form.getUsername(), form.getPassword()));
  }

  @GetMapping("/info")
  public ResponseVO<UserVO> userInfo() {
    return ResponseVO.success(userService.userInfo());
  }


}
