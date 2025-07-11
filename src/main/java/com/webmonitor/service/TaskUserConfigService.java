package com.webmonitor.service;

import com.webmonitor.entity.bo.TaskUserRecordPageBO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserRecordVO;

public interface TaskUserConfigService {

  PageResult<TaskUserRecordVO> queryUserTaskRecordsByPage(TaskUserRecordPageBO bo);

}
