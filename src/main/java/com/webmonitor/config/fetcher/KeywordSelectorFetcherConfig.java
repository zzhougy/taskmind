package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeywordSelectorFetcherConfig extends FetcherConfig {
  private String keyword;

}