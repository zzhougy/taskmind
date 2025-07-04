package com.webmonitor.config.fetcher;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class AIFetcherConfig extends FetcherConfig {
    private String userQuery;
    private String modelName;
}