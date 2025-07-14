package com.webmonitor.service;

import com.webmonitor.entity.bo.TaskUserConfigPageBO;
import com.webmonitor.entity.bo.TaskUserRecordDeleteBO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserConfigVO;

import com.webmonitor.entity.bo.TaskUserRecordStatusUpdateBO;
import com.webmonitor.entity.bo.TaskUserConfigCreateBO;

public interface TaskUserConfigService {

  void createTask(TaskUserConfigCreateBO bo);

  PageResult<TaskUserConfigVO> queryUserTaskConfigsByPage(TaskUserConfigPageBO bo);

  void updateTaskStatus(TaskUserRecordStatusUpdateBO bo);

  void delete(TaskUserRecordDeleteBO bo);
}
