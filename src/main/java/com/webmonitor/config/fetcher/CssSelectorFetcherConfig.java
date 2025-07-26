package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class CssSelectorFetcherConfig extends FetcherConfig {
  private Map<String, String> cssSelectors;
  private String wayToGetHtml;

}