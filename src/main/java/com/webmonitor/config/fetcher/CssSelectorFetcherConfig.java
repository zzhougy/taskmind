package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CssSelectorFetcherConfig extends FetcherConfig {
  private String cssSelector;
  private String wayToGetHtml;

}