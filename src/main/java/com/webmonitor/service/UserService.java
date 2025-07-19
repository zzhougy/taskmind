package com.webmonitor.service;

import com.webmonitor.entity.po.User;
import com.webmonitor.entity.vo.LoginVO;
import com.webmonitor.entity.vo.UserVO;

public interface UserService {

  public LoginVO login(String code);

  public LoginVO loginByUsernamePassword(String username, String password);

  public Boolean register(String username, String password);

  public User selectUser(String username, Boolean enable, Boolean deleted);

  UserVO userInfo();
}
