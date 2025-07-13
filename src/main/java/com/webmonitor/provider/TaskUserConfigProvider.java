package com.webmonitor.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.constant.ErrorCodeEnum;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.mapper.TaskUserConfigMapper;
import jakarta.annotation.Resource;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class TaskUserConfigProvider extends ServiceImpl<TaskUserConfigMapper, TaskUserConfig> {

  @Resource
  private TaskUserConfigMapper taskUserConfigMapper;


  public boolean save(TaskUserConfig taskUserConfig) {
    try {
      taskUserConfigMapper.insert(taskUserConfig);
    } catch (Exception e) {
      log.error("保存任务失败", e);
      if (e instanceof UncategorizedSQLException) {
        SQLException sqlException = ((UncategorizedSQLException) e).getSQLException();
        if (String.valueOf(ErrorCodeEnum.SQL_ERROR_USER_TASK_TOO_MANY.getCode()).equals(sqlException.getSQLState())) {
          throw new BusinessException(ErrorCodeEnum.SQL_ERROR_USER_TASK_TOO_MANY.getMsg());
        }
      }
      throw new RuntimeException("保存任务失败");
    }
    return true;
  }



  public Page<TaskUserConfig> queryUserTaskConfigsByPage(Integer userId, Integer pageNum, Integer pageSize) {
    MPJLambdaWrapper<TaskUserConfig> wrapper = new MPJLambdaWrapper<TaskUserConfig>()
            .selectAll(TaskUserConfig.class)
            .eq(TaskUserConfig::getUserId, userId)
            .eq(TaskUserConfig::getDeleted, false)
            .orderByDesc(TaskUserConfig::getUpdateTime);

    Page<TaskUserConfig> listPage = taskUserConfigMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

    return listPage;
  }
}
