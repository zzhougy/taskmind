package com.webmonitor.controller;

import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.LoginBO;
import com.webmonitor.entity.vo.LoginVO;
import com.webmonitor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
