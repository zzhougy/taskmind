package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SeleniumFetcherConfig extends FetcherConfig {
  private String cssSelector;
  private String xPath;
  private String driverPath;
  private int timeout = 10;
}