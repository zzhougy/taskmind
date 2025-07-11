package com.webmonitor.controller;

import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.TaskUserRecordPageBO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserRecordVO;
import com.webmonitor.service.TaskUserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user/task/config")
public class TaskUserConfigController {

  private final TaskUserRecordService taskUserRecordService;

  @Autowired
  public TaskUserConfigController(TaskUserRecordService taskUserRecordService) {
    this.taskUserRecordService = taskUserRecordService;
  }

  @GuestAccess // todo remove
  @PostMapping("/page")
  public ResponseVO<PageResult<TaskUserRecordVO>> getUserTaskRecords(@Validated @RequestBody TaskUserRecordPageBO bo) {
    PageResult<TaskUserRecordVO> result = taskUserRecordService.queryUserTaskRecordsByPage(bo);
    return ResponseVO.success(result);
  }
}