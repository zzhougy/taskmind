package com.webmonitor.config.observer;

import lombok.Data;

@Data
public abstract class ObserverConfig {
  private String type;
  private boolean enabled;

}
