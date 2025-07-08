package com.webmonitor.util;

import com.webmonitor.entity.base.ActiveUser;
import com.webmonitor.entity.po.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

@Slf4j
public class UserContext {

  public static Long getUserId() {
    return getUser().getId();
  }

  public static User getUser() {
    ActiveUser activeUser = getActiveUser();
    return activeUser.getUser();
  }

  public static ActiveUser getActiveUser() {
    ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
    return activeUser;
  }
}
