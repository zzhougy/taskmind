package com.webmonitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserRecordVO;
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
  public PageResult<TaskUserRecordVO> queryUserTaskRecordsByPage(Long userId, int pageNum, int pageSize) {

    Page<TaskUserRecord> taskUserRecordPage = taskUserRecordProvider.queryUserTaskRecordsByPage(userId, pageNum, pageSize);

    List<TaskUserRecordVO> taskUserRecordVOS = BeanUtil.copyToList(taskUserRecordPage.getRecords(), TaskUserRecordVO.class);


    PageResult<TaskUserRecordVO> result = new PageResult<>(taskUserRecordVOS, taskUserRecordPage.getTotal(), taskUserRecordPage.getCurrent(), taskUserRecordPage.getSize());
    return result;
  }


}