package com.webmonitor.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.mapper.TaskUserConfigMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class TaskUserConfigProvider extends ServiceImpl<TaskUserConfigMapper, TaskUserConfig> {

  @Resource
  private TaskUserConfigMapper taskUserConfigMapper;

  public Page<TaskUserConfig> queryUserTaskConfigsByPage(Integer userId, Integer pageNum, Integer pageSize) {
    MPJLambdaWrapper<TaskUserConfig> wrapper = new MPJLambdaWrapper<TaskUserConfig>()
            .selectAll(TaskUserConfig.class)
            .eq(TaskUserConfig::getUserId, userId)
            .orderByDesc(TaskUserConfig::getUpdateTime);

    Page<TaskUserConfig> listPage = taskUserConfigMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

    return listPage;
  }
}
