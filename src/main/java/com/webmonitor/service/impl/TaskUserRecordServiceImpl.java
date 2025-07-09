package com.webmonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.provider.TaskUserRecordProvider;
import com.webmonitor.service.TaskUserRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskUserRecordServiceImpl implements TaskUserRecordService {

  private final TaskUserRecordProvider taskUserRecordProvider;

  public TaskUserRecordServiceImpl(TaskUserRecordProvider taskUserRecordProvider) {
    this.taskUserRecordProvider = taskUserRecordProvider;
  }

  @Override
  public void save(TaskUserRecord taskUserRecord) {
    taskUserRecordProvider.save(taskUserRecord);
  }

  @Override
  public void saveBatch(List<TaskUserRecord> records) {
    taskUserRecordProvider.saveBatch(records);
  }

  @Override
  public PageResult<TaskUserRecord> queryUserTaskRecordsByPage(Long userId, int pageNum, int pageSize) {
    Page<TaskUserRecord> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<TaskUserRecord> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(TaskUserRecord::getUserId, userId);
    // 按创建时间倒序排列
    queryWrapper.orderByDesc(TaskUserRecord::getCreateTime);
    Page<TaskUserRecord> resultPage = taskUserRecordProvider.page(page, queryWrapper);
    return new PageResult<>(resultPage.getRecords(), resultPage.getTotal(), pageNum, pageSize);
  }
}