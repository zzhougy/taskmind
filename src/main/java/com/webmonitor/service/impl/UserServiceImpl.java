package com.webmonitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.webmonitor.entity.vo.UserVO;
import com.webmonitor.provider.UserProvider;
import com.webmonitor.service.UserService;
import com.webmonitor.util.UserContext;
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
    String token = JWTUtils.sign(user.getUsername(), null, jwtSecret);
    JWTToken jwtToken = new JWTToken(token);
    try {
      SecurityUtils.getSubject().login(jwtToken);
    } catch (AuthenticationException e) {
      throw new BusinessException(ErrorCodeEnum.PASSWORD_DEFINED.getCode(), ErrorCodeEnum.PASSWORD_DEFINED.getMsg());
    }
    return LoginVO.builder().token( token).build();
  }

  @Override
  public LoginVO loginByUsernamePassword(String username, String password) {
    User user = userProvider.selectOne(username, true, false);
    if (user == null) {
      throw new BusinessException(ErrorCodeEnum.PASSWORD_DEFINED);
    }
//    if (!BCrypt.checkpw(password, user.getPassword())) {
//      throw new BusinessException(ErrorCodeEnum.PASSWORD_DEFINED.getCode(), ErrorCodeEnum.PASSWORD_DEFINED.getMsg());
//    }
    String token = JWTUtils.sign(user.getUsername(), password, jwtSecret);
    JWTToken jwtToken = new JWTToken(token);
    try {
      SecurityUtils.getSubject().login(jwtToken);
    } catch (AuthenticationException e) {
      throw new BusinessException(ErrorCodeEnum.PASSWORD_DEFINED);
    }
    return LoginVO.builder().token(token).build();
  }

  @Override
  public Boolean register(String username, String password) {
    User existingUser = userProvider.selectOne(username, null, null);
    if (existingUser != null) {
      throw new BusinessException(ErrorCodeEnum.USER_ALREADY_EXISTS.getCode(), ErrorCodeEnum.USER_ALREADY_EXISTS.getMsg());
    }
    User user = new User();
    user.setUsername(username);
//    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    user.setPassword(password);
    userProvider.save(user);
    return true;
  }

  @Override
  public User selectUser(String username, Boolean enable, Boolean deleted) {
    return userProvider.selectOne(username, enable, deleted);
  }

  @Override
  public UserVO userInfo() {
    User user1 = UserContext.getUser();
    UserVO userVO = BeanUtil.copyProperties(user1, UserVO.class);
    return userVO;
  }


}
