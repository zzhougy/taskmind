package com.webmonitor.config.fetcher;

import lombok.Data;

@Data
public abstract class FetcherConfig {
  private String type;
  private String name;
  private String url;
  private int intervalSeconds;
  private boolean enabled;

}
