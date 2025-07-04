package com.webmonitor.service.ai;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.fetcher.XPathFetcherConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.util.JsoupUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class AITools {

  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;

  @Tool(description = "Set the timing task according to the user 's content description")
  boolean setUpTimingTask(String url, @ToolParam(description = "Must correct cron format; Cron expression must consist of 6 fields") String cron, String contentWithoutTimeAndUrl) throws Exception {
    log.info("[setUpTimingTask] url: {}, cron: {}, contentWithoutTimeAndUrl: {}", url, cron, contentWithoutTimeAndUrl);
    XPathFetcherConfig config = new XPathFetcherConfig();
    config.setUrl(url);
    config.setType("XPathFetcher");
    config.setName("XPathMonitor");
    config.setEnabled(true);
    config.setCron(cron);
    String xPath = JsoupUtil.getXPathFromAI(url, "zhipu", contentWithoutTimeAndUrl, webMonitorFactory.loadAIModels());
    config.setXPath(xPath);

    List<ObserverConfig> observerConfigs = webMonitorFactory.loadObserverConfigs();
    // 随机生成一个long
    long userId = (long) (Math.random() * 1000000000000000000L);
    monitor.startMonitoring(userId, List.of(config), observerConfigs, webMonitorFactory.loadAIModels());
    return true;
  }

}