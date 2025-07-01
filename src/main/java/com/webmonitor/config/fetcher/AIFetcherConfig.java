package com.webmonitor.config.fetcher;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AIFetcherConfig extends FetcherConfig {
    private String userQuery;
    private String modelName;
}