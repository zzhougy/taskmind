package com.webmonitor.service.observer;

import com.webmonitor.core.WebContent;
import com.webmonitor.entity.po.TaskUserConfig;

import java.util.List;

public interface WebObserver {

  void send(List<WebContent> webContents);
  default void saveDB(TaskUserConfig config, List<WebContent> webContents){

  }

}
