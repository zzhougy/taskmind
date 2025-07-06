package com.webmonitor.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.config.jwt.JWTToken;
import com.webmonitor.config.jwt.JWTUtils;
import com.webmonitor.constant.ErrorCodeEnum;
import com.webmonitor.entity.po.User;
import com.webmonitor.entity.vo.LoginVO;
import com.webmonitor.provider.UserProvider;
import com.webmonitor.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {
  public static final String STRING = "微信用户";

  @Value("${wx.app-id}")
  private String appId;

  @Value("${wx.app-secret}")
  private String appSecret;

  @Value("${jwt.secret}")
  private String jwtSecret;


  @Autowired
  private UserProvider userProvider;

  private String getOpenId(String code) {
    String url = "https://api.weixin.qq.com/sns/jscode2session";
    HashMap map = new HashMap();
    map.put("appid", appId);
    map.put("secret", appSecret);
    map.put("js_code", code);
    map.put("grant_type", "authorization_code");
    String response = HttpUtil.post(url, map);
    JSONObject json = JSONUtil.parseObj(response);
    String openId = json.getStr("openid");
    if (openId == null || openId.length() == 0) {
      throw new RuntimeException(response);
    }
    return openId;
  }


  @Override
  public LoginVO login(String code) {
    String openId = getOpenId(code);
    User user = userProvider.getOne(new QueryWrapper<User>().lambda().eq(User::getOpenId, openId));
    if (user == null) {
      // insert
      user = new User();
      user.setOpenId(openId);
      user.setUsername(STRING + "_" + RandomUtil.randomNumbers(6));
      userProvider.save(user);
    }
    //生成Token
    String token = JWTUtils.sign(user.getUsername(), jwtSecret);
    JWTToken jwtToken = new JWTToken(token);
    try {
      SecurityUtils.getSubject().login(jwtToken);
    } catch (AuthenticationException e) {
      throw new BusinessException(ErrorCodeEnum.PASSWORD_DEFINED.getCode(), ErrorCodeEnum.PASSWORD_DEFINED.getMsg());
    }
    return LoginVO.builder().token( token).build();
  }

  @Override
  public User selectUserByName(String username) {
    return userProvider.getOne(new QueryWrapper<User>().lambda().eq(User::getUsername, username));
  }


}
