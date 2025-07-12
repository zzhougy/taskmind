package com.webmonitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webmonitor.entity.bo.TaskUserRecordPageBO;
import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserRecordVO;
import com.webmonitor.provider.TaskUserRecordProvider;
import com.webmonitor.service.TaskUserRecordService;
import com.webmonitor.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
  public PageResult<TaskUserRecordVO> queryUserTaskRecordsByPage(TaskUserRecordPageBO bo) {

    Integer userId = UserContext.getUserId();
    Page<TaskUserRecord> taskUserRecordPage = taskUserRecordProvider.queryUserTaskRecordsByPage(userId, bo.getPageNum(), bo.getPageSize());


    List<TaskUserRecordVO> collect = taskUserRecordPage.getRecords().stream().map(o -> {
      TaskUserRecordVO taskUserRecordVO = BeanUtil.copyProperties(o, TaskUserRecordVO.class);
      taskUserRecordVO.setUserInput(o.getTaskUserConfig().getUserInput());
      return taskUserRecordVO;
    }).collect(Collectors.toList());


    PageResult<TaskUserRecordVO> result = new PageResult<>(collect, taskUserRecordPage.getTotal(),
            taskUserRecordPage.getCurrent(), taskUserRecordPage.getSize());
    return result;
  }


}