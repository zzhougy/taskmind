package com.webmonitor.service.observer;

import com.webmonitor.core.WebContent;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.entity.po.TaskUserRecord;
import com.webmonitor.service.TaskUserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DBWebObserver implements WebObserver {

  private final TaskUserRecordService taskUserRecordService;

  public DBWebObserver(TaskUserRecordService taskUserRecordService) {
    this.taskUserRecordService = taskUserRecordService;
  }


  public void send(List<WebContent> webContents) {
    log.info("开始保存内容到数据库");
    if (webContents == null || webContents.isEmpty()) {
      log.warn("收到空内容");
    }
  }

  @Override
  public void saveDB(TaskUserConfig config, List<WebContent> webContents) {
    if (config == null || webContents == null || webContents.isEmpty()) {
      log.warn("收到空的内容或者不存db");
      return;
    }

    log.info("开始保存内容到数据库");
    List<TaskUserRecord> records = new ArrayList<>();
    try {
      for (WebContent content : webContents) {
        TaskUserRecord record = new TaskUserRecord();
        record.setTaskConfigId(config.getId());
        record.setUserId(config.getUserId());
        record.setContentTitle(content.getTitle());
        record.setContentDetail(content.getDescription());
        record.setContentUrl(content.getUrl());
        record.setContentDateStr(content.getDateStr());
        records.add(record);
      }
      
      if (!records.isEmpty()) {
        taskUserRecordService.saveBatch(records);
        log.info("成功插入 {} 条记录到数据库", records.size());
      } else {
        log.info("没有有效记录需要保存到数据库");
      }
    } catch (Exception e) {
      log.error("数据库插入失败", e);
    }
  }


}