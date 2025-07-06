package com.webmonitor.service;

import com.webmonitor.entity.po.User;
import com.webmonitor.entity.vo.LoginVO;

public interface UserService {

  public LoginVO login(String code);

  public User selectUserByName(String username);


}
