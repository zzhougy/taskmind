package com.webmonitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.entity.vo.PageResult;
import java.util.List;

public interface TaskUserRecordService {

  public void save(TaskUserRecord taskUserRecord);
  public void saveBatch(List<TaskUserRecord> records);
  PageResult<TaskUserRecord> queryUserTaskRecordsByPage(Long userId, int pageNum, int pageSize);

}
