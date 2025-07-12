package com.webmonitor.service.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class UserSchedulerService {

  // 存储用户的任务引用
  private final Map<Integer, ScheduledFuture<?>> userTasks = new ConcurrentHashMap<>();

  // 存储任务执行状态
  private final Map<Integer, TaskExecutionState> taskStates = new ConcurrentHashMap<>();

  private final TaskScheduler taskScheduler;

  @Autowired
  public UserSchedulerService(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  // 创建/更新定时任务
  public void scheduleTaskForUser(Integer taskUserConfigId, String cronExpression, Runnable task) {
    // 取消现有任务
    cancelTaskForUser(taskUserConfigId);

    // 创建新的Cron触发器
    CronTrigger trigger = new CronTrigger(cronExpression);

    // 获取或创建任务状态
    TaskExecutionState state = taskStates.computeIfAbsent(taskUserConfigId, k -> new TaskExecutionState());

    // 添加执行状态跟踪和跳过逻辑
    Runnable monitoredTask = createMonitoredTask(taskUserConfigId, task, state);

    // 调度任务
    ScheduledFuture<?> future = taskScheduler.schedule(monitoredTask, trigger);

    // 存储任务引用
    userTasks.put(taskUserConfigId, future);

    log.info("为任务 {} 创建了新的定时任务，Cron表达式: {}", taskUserConfigId, cronExpression);
  }

  private Runnable createMonitoredTask(Integer taskUserConfigId, Runnable originalTask, TaskExecutionState state) {
    return () -> {
      log.info("开始执行任务: {} 的任务", taskUserConfigId);
      // 检查前一个任务是否仍在运行
      if (state.isRunning()) {
        log.warn("为任务 {} 跳过任务执行 - 前一个任务仍在运行", taskUserConfigId);
        return;
      }

      try {
        // 标记任务开始执行
        state.markRunning();

        // 执行实际任务
        originalTask.run();

        log.debug("任务 {} 的任务执行完成", taskUserConfigId);
      } catch (Exception e) {
        log.error("任务 {} 的任务执行失败", taskUserConfigId, e);
      } finally {
        // 确保任务状态被重置
        state.markCompleted();
      }
    };
  }

  public void cancelTaskForUser(Integer taskUserConfigId) {
    ScheduledFuture<?> future = userTasks.get(taskUserConfigId);
    if (future != null) {
        future.cancel(true);
        userTasks.remove(taskUserConfigId);

        // 重置任务状态
        TaskExecutionState state = taskStates.get(taskUserConfigId);
        if (state != null) {
            state.reset();
        }

        log.info("已取消任务 {} 的定时任务", taskUserConfigId);
    }
}

  public Map<Integer, String> getActiveTasks() {
    Map<Integer, String> activeTasks = new HashMap<>();
    for (Map.Entry<Integer, ScheduledFuture<?>> entry : userTasks.entrySet()) {
      if (!entry.getValue().isCancelled()) {
        Integer userId = entry.getKey();
        TaskExecutionState state = taskStates.get(userId);
        String status = state != null && state.isRunning() ? "RUNNING" : "SCHEDULED";
        activeTasks.put(userId, status);
      }
    }
    return activeTasks;
  }

  public String getUserTaskStatus(Integer userId) {
    TaskExecutionState state = taskStates.get(userId);
    if (state == null) return "NOT_SCHEDULED";
    if (state.isRunning()) return "RUNNING";
    return "SCHEDULED";
  }

  // 任务执行状态内部类
  private static class TaskExecutionState {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ReentrantLock lock = new ReentrantLock();
    private long startTime;

    public boolean isRunning() {
      return running.get();
    }

    public void markRunning() {
      lock.lock();
      try {
        running.set(true);
        startTime = System.currentTimeMillis();
      } finally {
        lock.unlock();
      }
    }

    public void markCompleted() {
      lock.lock();
      try {
        running.set(false);
        long duration = System.currentTimeMillis() - startTime;
        log.debug("任务执行耗时: {} ms", duration);
      } finally {
        lock.unlock();
      }
    }

    public void reset() {
      lock.lock();
      try {
        running.set(false);
      } finally {
        lock.unlock();
      }
    }
  }
}