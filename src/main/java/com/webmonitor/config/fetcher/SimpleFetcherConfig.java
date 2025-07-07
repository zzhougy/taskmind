package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleFetcherConfig extends FetcherConfig {
  private String content;
}