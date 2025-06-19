package com.webmonitor.config.observer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmailObserverConfig extends ObserverConfig {
  private String host;
  private String port;
  private String username;
  private String password;
  private String from;
  private String to;

}