package com.webmonitor.controller;

import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.entity.ResponseVO;
import com.webmonitor.entity.vo.PageResult;
import com.webmonitor.entity.vo.TaskUserRecordVO;
import com.webmonitor.service.TaskUserRecordService;
import com.webmonitor.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务用户记录控制器
 * 处理用户任务记录的分页查询请求
 */
@RestController
@RequestMapping("/v1/taskUserRecord")
public class TaskUserRecordController {

  private final TaskUserRecordService taskUserRecordService;

  @Autowired
  public TaskUserRecordController(TaskUserRecordService taskUserRecordService) {
    this.taskUserRecordService = taskUserRecordService;
  }

  /**
   * 分页查询当前用户的任务记录
   *
   * @param pageNum  页码，默认为1
   * @param pageSize 每页大小，默认为10
   * @return 分页查询结果
   */
  @GuestAccess // todo remove
  @GetMapping
  public ResponseVO<PageResult<TaskUserRecordVO>> getUserTaskRecords(
          @RequestParam(defaultValue = "1") Integer pageNum,
          @RequestParam(defaultValue = "10") Integer pageSize) {
    // 从上下文获取当前用户ID
    Long userId = UserContext.getUserId();
    // 调用服务层进行分页查询
    PageResult<TaskUserRecordVO> result = taskUserRecordService.queryUserTaskRecordsByPage(userId, pageNum, pageSize);
    return ResponseVO.success(result);
  }
}