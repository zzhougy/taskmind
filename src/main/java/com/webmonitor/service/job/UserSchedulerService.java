package com.webmonitor.service.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class UserSchedulerService {

  private final Map<Long, ScheduledFuture<?>> userTasks = new ConcurrentHashMap<>();
  private final TaskScheduler taskScheduler;

  @Autowired
  public UserSchedulerService(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  // 创建/更新定时任务
  public void scheduleTaskForUser(Long userId, String cronExpression, Runnable task) {
    // 取消现有任务
    cancelTaskForUser(userId);

    // 创建新的Cron触发器
    CronTrigger trigger = new CronTrigger(cronExpression);

    // 调度任务
    ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);

    // 存储任务引用
    userTasks.put(userId, future);
  }

  // 取消特定用户的定时任务
  public void cancelTaskForUser(Long userId) {
    ScheduledFuture<?> future = userTasks.get(userId);
    if (future != null) {
      future.cancel(true);
      userTasks.remove(userId);
    }
  }

  // 获取所有活动任务
  public Map<Long, String> getActiveTasks() {
    Map<Long, String> activeTasks = new HashMap<>();
    for (Map.Entry<Long, ScheduledFuture<?>> entry : userTasks.entrySet()) {
      if (!entry.getValue().isCancelled()) {
        activeTasks.put(entry.getKey(), "ACTIVE");
      }
    }
    return activeTasks;
  }
}