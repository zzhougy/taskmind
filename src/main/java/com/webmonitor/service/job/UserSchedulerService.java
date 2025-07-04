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
  private final Map<Long, ScheduledFuture<?>> userTasks = new ConcurrentHashMap<>();

  // 存储任务执行状态
  private final Map<Long, TaskExecutionState> taskStates = new ConcurrentHashMap<>();

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

    // 获取或创建任务状态
    TaskExecutionState state = taskStates.computeIfAbsent(userId, k -> new TaskExecutionState());

    // 添加执行状态跟踪和跳过逻辑
    Runnable monitoredTask = createMonitoredTask(userId, task, state);

    // 调度任务
    ScheduledFuture<?> future = taskScheduler.schedule(monitoredTask, trigger);

    // 存储任务引用
    userTasks.put(userId, future);

    log.info("为用户 {} 创建了新的定时任务，Cron表达式: {}", userId, cronExpression);
  }

  private Runnable createMonitoredTask(Long userId, Runnable originalTask, TaskExecutionState state) {
    return () -> {
      log.info("开始执行用户 {} 的任务", userId);
      // 检查前一个任务是否仍在运行
      if (state.isRunning()) {
        log.warn("为用户 {} 跳过任务执行 - 前一个任务仍在运行", userId);
        return;
      }

      try {
        // 标记任务开始执行
        state.markRunning();

        // 执行实际任务
        originalTask.run();

        log.debug("用户 {} 的任务执行完成", userId);
      } catch (Exception e) {
        log.error("用户 {} 的任务执行失败", userId, e);
      } finally {
        // 确保任务状态被重置
        state.markCompleted();
      }
    };
  }

  public void cancelTaskForUser(Long userId) {
    ScheduledFuture<?> future = userTasks.get(userId);
    if (future != null) {
      future.cancel(true);
      userTasks.remove(userId);

      // 重置任务状态
      TaskExecutionState state = taskStates.get(userId);
      if (state != null) {
        state.reset();
      }

      log.info("已取消用户 {} 的定时任务", userId);
    }
  }

  public Map<Long, String> getActiveTasks() {
    Map<Long, String> activeTasks = new HashMap<>();
    for (Map.Entry<Long, ScheduledFuture<?>> entry : userTasks.entrySet()) {
      if (!entry.getValue().isCancelled()) {
        Long userId = entry.getKey();
        TaskExecutionState state = taskStates.get(userId);
        String status = state != null && state.isRunning() ? "RUNNING" : "SCHEDULED";
        activeTasks.put(userId, status);
      }
    }
    return activeTasks;
  }

  public String getUserTaskStatus(Long userId) {
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