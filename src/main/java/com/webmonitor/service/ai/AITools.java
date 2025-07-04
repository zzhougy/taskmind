package com.webmonitor.service.ai;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.fetcher.XPathFetcherConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.util.JsoupUtil;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class AITools {

  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;

  @Tool(description = "Set the timing task according to the user 's content description")
  boolean setUpTimingTask(String url, String cron, String contentWithoutTimeAndUrl) throws Exception {
//    monitor.startMonitoring("https://www.baidu.com", cron, contentWithoutTimeAndUrl);

    XPathFetcherConfig config = new XPathFetcherConfig();
    config.setUrl(url);
    config.setType("XPathFetcher");
    config.setName("XPathMonitor");
    config.setEnabled(true);
    config.setCron(cron);
    String xPath = JsoupUtil.getXPathFromAI(url, "zhipu", contentWithoutTimeAndUrl, webMonitorFactory.loadAIModels());
    config.setXPath(xPath);

    List<ObserverConfig> observerConfigs = webMonitorFactory.loadObserverConfigs();
    monitor.startMonitoring(List.of(config), observerConfigs, webMonitorFactory.loadAIModels());
    return true;
  }

}