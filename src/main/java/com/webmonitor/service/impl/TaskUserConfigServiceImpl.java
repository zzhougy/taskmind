package com.webmonitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.bo.TaskUserConfigPageBO;
import com.webmonitor.entity.bo.TaskUserRecordDeleteBO;
import com.webmonitor.entity.bo.TaskUserRecordStatusUpdateBO;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserConfigVO;
import com.webmonitor.provider.TaskUserConfigProvider;
import com.webmonitor.service.TaskUserConfigService;
import com.webmonitor.service.job.UserSchedulerService;
import com.webmonitor.util.CronUtil;
import com.webmonitor.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskUserConfigServiceImpl implements TaskUserConfigService {

  @Resource
  private TaskUserConfigProvider taskUserConfigProvider;

  @Resource
  private WebMonitor webMonitor;

  @Resource
  private UserSchedulerService schedulerService;


  @Override
  public PageResult<TaskUserConfigVO> queryUserTaskConfigsByPage(TaskUserConfigPageBO bo) {

    Integer userId = UserContext.getUserId();
    Page<TaskUserConfig> taskUserConfigPage = taskUserConfigProvider
            .queryUserTaskConfigsByPage(userId, bo.getPageNum(), bo.getPageSize());

    List<TaskUserConfigVO> collect = taskUserConfigPage.getRecords().stream().map(taskUserConfig -> {
      TaskUserConfigVO taskUserConfigVO = BeanUtil.copyProperties(taskUserConfig, TaskUserConfigVO.class);
      taskUserConfigVO.setExecutionTime(CronUtil.convertDescription(taskUserConfig.getCronExpression()));
      return taskUserConfigVO;
    }).collect(Collectors.toList());


    PageResult<TaskUserConfigVO> result = new PageResult<>(collect, taskUserConfigPage.getTotal(),
            taskUserConfigPage.getCurrent(), taskUserConfigPage.getSize());
    return result;
  }

  @Transactional
  @Override
  public void updateTaskStatus(TaskUserRecordStatusUpdateBO bo) {
    TaskUserConfig config = taskUserConfigProvider.getById(bo.getId());
    Integer userId = UserContext.getUserId();
    if (config == null || config.getDeleted() || !userId.equals(config.getUserId())) {
      throw new BusinessException("不存在");
    }

    if (Objects.equals(config.getEnable(), bo.getEnable())) {
      throw new BusinessException("无需重复操作");
    }

    // 更新数据库状态
    config.setEnable(bo.getEnable());
    boolean updateSuccess = taskUserConfigProvider.updateById(config);
    if (!updateSuccess) {
      log.error("更新任务状态失败，taskId: {}", config.getId());
      throw new BusinessException("操作失败");
    }

    // 处理定时任务
    if (bo.getEnable()) {
      // 启用任务：创建并调度
      ContentFetcher fetcher = webMonitor.createContentFetcherFromTaskConfig(config);
      if (fetcher == null) {
        log.error("创建内容获取器失败，无法启用任务，taskId: {}", bo.getId());
        throw new BusinessException("操作失败");
      }
      Runnable task = webMonitor.createUserTask(config, fetcher);
      schedulerService.scheduleTaskForUser(bo.getId(), config.getCronExpression(), task);
      log.info("已启用任务，taskId: {}", bo.getId());
    } else {
      // 禁用任务：取消调度
      schedulerService.cancelTaskForUser(bo.getId());
      log.info("已禁用任务，taskId: {}", bo.getId());
    }
  }

  @Transactional
  @Override
  public void delete(TaskUserRecordDeleteBO bo) {
    TaskUserConfig config = taskUserConfigProvider.getById(bo.getId());
    if (config == null || config.getDeleted() || !UserContext.getUserId().equals(config.getUserId())) {
      throw new BusinessException("不存在");
    }

    config.setDeleted(true);
    boolean updateSuccess = taskUserConfigProvider.updateById(config);
    if (!updateSuccess) {
      log.error("删除任务失败，taskId: {}", config.getId());
      throw new BusinessException("操作失败");
    }
    // 禁用任务：取消调度
    schedulerService.cancelTaskForUser(bo.getId());
    log.info("已禁用任务，taskId: {}", bo.getId());
  }
}