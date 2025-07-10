package com.webmonitor.service;

import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserRecordVO;

import java.util.List;

public interface TaskUserRecordService {

  public void save(TaskUserRecord taskUserRecord);
  public void saveBatch(List<TaskUserRecord> records);
  PageResult<TaskUserRecordVO> queryUserTaskRecordsByPage(Long userId, int pageNum, int pageSize);

}
