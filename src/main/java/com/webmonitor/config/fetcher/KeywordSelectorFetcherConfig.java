package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeywordSelectorFetcherConfig extends FetcherConfig {
  private Map<String, String> keywords;
  private String wayToGetHtml;

}