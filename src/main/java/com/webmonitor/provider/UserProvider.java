package com.webmonitor.provider;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webmonitor.entity.po.User;
import com.webmonitor.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserProvider extends ServiceImpl<UserMapper, User> {

}
