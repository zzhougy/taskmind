package com.webmonitor.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webmonitor.entity.po.User;
import com.webmonitor.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserProvider extends ServiceImpl<UserMapper, User> {

    public User selectOne(String username, Boolean enable, Boolean deleted) {
      return getOne(new QueryWrapper<User>().lambda()
              .eq(User::getUsername, username)
              .eq(enable != null, User::getEnable, enable)
              .eq(deleted != null, User::getDeleted, deleted)
      );

    }
}
