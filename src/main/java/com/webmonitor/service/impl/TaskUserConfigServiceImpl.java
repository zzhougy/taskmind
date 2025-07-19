package com.webmonitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.config.exception.SystemException;
import com.webmonitor.constant.TaskTypeEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.bo.TaskUserConfigCreateBO;
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
  public void createTask(TaskUserConfigCreateBO bo) {
    Integer userId = UserContext.getUserId();
    if (userId == null) {
      throw new BusinessException("用户未登录");
    }

    // 验证时间参数
    if (bo.getHour() != null && (bo.getHour() < 0 || bo.getHour() > 23)) {
      throw new BusinessException("小时必须在0-23之间");
    }
    if (bo.getMinute() != null && (bo.getMinute() < 0 || bo.getMinute() > 59)) {
      throw new BusinessException("分钟必须在0-59之间");
    }

    // 生成cron表达式
    String cronExpression = CronUtil.generateCronExpression(bo.getFrequency(), null, bo.getHour(), bo.getMinute(), bo.getMonth(), bo.getDay(), bo.getInterval(), bo.getDayOfWeek(), bo.getYear());
    if (cronExpression == null) {
      throw new SystemException("cron表达式生成失败");
    }

    // 创建任务配置PO
    TaskUserConfig config = new TaskUserConfig();
    config.setTaskContent(bo.getTaskContent());
    config.setUserId(userId);
    config.setTaskTypeCode(TaskTypeEnum.SIMPLE.getCode());
    config.setCronExpression(cronExpression);
    config.setEnable(true);
    config.setDeleted(false);

    // 保存到数据库
    boolean saveSuccess = taskUserConfigProvider.save(config);
    if (!saveSuccess) {
      log.error("保存任务配置失败");
      throw new BusinessException("操作失败");
    }

    // 创建并调度任务
    ContentFetcher fetcher = webMonitor.createContentFetcherFromTaskConfig(config);
    if (fetcher == null) {
      log.error("创建内容获取器失败，taskId: {}", config.getId());
      throw new BusinessException("操作失败");
    }
    Runnable task = webMonitor.createUserTask(config, fetcher);
    schedulerService.scheduleTaskForUser(config.getId(), cronExpression, task);
    log.info("创建并调度任务成功，taskId: {}", config.getId());
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