package com.webmonitor.service;

import com.webmonitor.entity.bo.TaskUserConfigPageBO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserConfigVO;

import com.webmonitor.entity.bo.UpdateTaskStatusBO;

public interface TaskUserConfigService {

  PageResult<TaskUserConfigVO> queryUserTaskConfigsByPage(TaskUserConfigPageBO bo);

  void updateTaskStatus(UpdateTaskStatusBO bo);

}
