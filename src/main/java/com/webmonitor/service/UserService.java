package com.webmonitor.service;

import com.webmonitor.entity.po.User;
import com.webmonitor.entity.vo.LoginVO;
import com.webmonitor.entity.vo.UserVO;

public interface UserService {

  public LoginVO login(String code);

  public User selectUserByName(String username);


  UserVO userInfo();
}
