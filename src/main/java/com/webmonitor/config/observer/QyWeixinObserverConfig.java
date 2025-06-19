package com.webmonitor.config.observer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QyWeixinObserverConfig extends ObserverConfig {
  private String webhookBaseUrl;
  private String webhookKey;
}