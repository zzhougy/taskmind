package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class XPathFetcherConfig extends FetcherConfig {
  private String xPath;
  private String userAgent;

}