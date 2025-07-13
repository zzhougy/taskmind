package com.webmonitor.service;

import com.webmonitor.entity.bo.TaskUserConfigPageBO;
import com.webmonitor.entity.bo.TaskUserRecordDeleteBO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserConfigVO;

import com.webmonitor.entity.bo.TaskUserRecordStatusUpdateBO;

public interface TaskUserConfigService {

  PageResult<TaskUserConfigVO> queryUserTaskConfigsByPage(TaskUserConfigPageBO bo);

  void updateTaskStatus(TaskUserRecordStatusUpdateBO bo);

  void delete(TaskUserRecordDeleteBO bo);
}
