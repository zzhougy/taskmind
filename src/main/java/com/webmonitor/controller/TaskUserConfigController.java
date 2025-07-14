package com.webmonitor.controller;

import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.bo.TaskUserConfigPageBO;
import com.webmonitor.entity.bo.TaskUserRecordDeleteBO;
import com.webmonitor.entity.bo.TaskUserRecordStatusUpdateBO;
import com.webmonitor.entity.bo.TaskUserConfigCreateBO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserConfigVO;
import com.webmonitor.service.TaskUserConfigService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user/task/config")
public class TaskUserConfigController {

  @Resource
  private TaskUserConfigService taskUserConfigService;


  @GuestAccess // todo remove
  @PostMapping("/page")
  public ResponseVO<PageResult<TaskUserConfigVO>> getUserTaskRecords(@Validated @RequestBody TaskUserConfigPageBO bo) {
    PageResult<TaskUserConfigVO> result = taskUserConfigService.queryUserTaskConfigsByPage(bo);
    return ResponseVO.success(result);
  }

  @GuestAccess // todo remove
  @PutMapping("/status")
  public ResponseVO<Boolean> updateTaskStatus(@Validated @RequestBody TaskUserRecordStatusUpdateBO bo) {
    taskUserConfigService.updateTaskStatus(bo);
    return ResponseVO.success(true);
  }

  @GuestAccess // todo remove
  @PostMapping("/create")
  public ResponseVO<Boolean> createTask(@Validated @RequestBody TaskUserConfigCreateBO bo) {
    taskUserConfigService.createTask(bo);
    return ResponseVO.success(true);
  }

  @GuestAccess // todo remove
  @DeleteMapping
  public ResponseVO<Boolean> delete(@Validated @RequestBody TaskUserRecordDeleteBO bo) {
    taskUserConfigService.delete(bo);
    return ResponseVO.success(true);
  }
}