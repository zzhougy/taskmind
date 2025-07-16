package com.webmonitor.service.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.quartz.CronExpression;
import java.util.Date;
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
  // 保存下一次调度句柄，方便取消
  private final Map<Integer, ScheduledFuture<?>> nextFireTasks = new ConcurrentHashMap<>();

  private final TaskScheduler taskScheduler;

  @Autowired
  public UserSchedulerService(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  // 创建/更新定时任务
  public void scheduleTaskForUser(Integer taskUserConfigId, String cronExpression, Runnable task) {
    // 取消现有任务
    cancelTaskForUser(taskUserConfigId);

    CronExpression expr = null;
    try {
      expr = new CronExpression(cronExpression); // 支持 7 位
    } catch (Exception e) {
      log.error("cron 表达式非法：{}", cronExpression, e);
      throw new IllegalArgumentException("Invalid cron: " + cronExpression, e);
    }

    TaskExecutionState state = taskStates.computeIfAbsent(taskUserConfigId, k -> new TaskExecutionState());

    Runnable monitoredTask = createMonitoredTask(taskUserConfigId, task, state);
    scheduleNext(taskUserConfigId, expr, monitoredTask);

    log.info("为任务 {} 创建了新的定时任务，Cron表达式: {}", taskUserConfigId, cronExpression);
  }

  /**
   * 递归调度：算下一次时间 → 提交延时任务 → 任务跑完再算下一次
   */
  private void scheduleNext(Integer id, CronExpression expr, Runnable task) {
    Date next = expr.getNextValidTimeAfter(new Date());
    if (next == null) {
      log.warn("任务 {} 无下一次触发时间，停止调度", id);
      return;
    }

    ScheduledFuture<?> future = taskScheduler.schedule(() -> {
      try {
        task.run();
      } finally {
        // 任务结束后继续排下一次
        scheduleNext(id, expr, task);
      }
    }, next);

    nextFireTasks.put(id, future);
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

  /**
   * 取消任务：同时取消当前等待的 future
   */
  public void cancelTaskForUser(Integer taskUserConfigId) {
    ScheduledFuture<?> future = nextFireTasks.remove(taskUserConfigId);
    if (future != null) {
      future.cancel(true);
    }

    future = userTasks.remove(taskUserConfigId);
    if (future != null) {
      future.cancel(true);
    }

    TaskExecutionState state = taskStates.get(taskUserConfigId);
    if (state != null) {
      state.reset();
    }
    log.info("已取消任务 {} 的定时任务", taskUserConfigId);
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