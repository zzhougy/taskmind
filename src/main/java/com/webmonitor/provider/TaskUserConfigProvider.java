package com.webmonitor.provider;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.mapper.TaskUserConfigMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskUserConfigProvider extends ServiceImpl<TaskUserConfigMapper, TaskUserConfig> {

}
