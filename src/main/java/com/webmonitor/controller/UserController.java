package com.webmonitor.controller;

import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.LoginBO;
import com.webmonitor.entity.vo.LoginVO;
import com.webmonitor.entity.vo.UserVO;
import com.webmonitor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/login")
  public ResponseVO<LoginVO> login(@Valid @RequestBody LoginBO form) {
    return ResponseVO.success(userService.login(form.getCode()));
  }

  @GetMapping("/info")
  public ResponseVO<UserVO> userInfo() {
    return ResponseVO.success(userService.userInfo());
  }


}
