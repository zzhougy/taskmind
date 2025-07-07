package com.webmonitor.service.ai;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.fetcher.CssSelectorFetcherConfig;
import com.webmonitor.config.fetcher.SimpleFetcherConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.util.JsoupUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class AITools {

  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;

  @Tool(description = "设置定时任务：1) 简单提醒任务 2) 动态获取任务。对于网页内容获取任务，需在content参数中明确指定操作指令（如GET_FIRST_HOT）。")
  boolean setUpTimingTask(
          @Nullable
          @ToolParam(description = "仅动态获取任务需要，如'https://top.baidu.com/board?tab=realtime'。简单提醒任务留空", required = false)
          String url,

          @ToolParam(description = "标准6字段cron表达式，必须包含秒、分、时、日、月、周。示例: '0 0 10 * * ?'表示每天10点")
          String cron,

          @ToolParam(description = "任务内容描述："
                  + "1) 简单提醒任务 - 直接填写提醒内容（如'吃药'）"
                  + "2) 动态获取任务 - 填写操作指令格式：'指令::描述'，如'GET_FIRST_HOT::获取热搜标题'")
          String content
  ) throws Exception  {
    log.info("[setUpTimingTask] url: {}, cron: {}, content: {}", url, cron, content);
//    XPathFetcherConfig config = new XPathFetcherConfig();
//    config.setUrl(url);
//    config.setType("XPathFetcher");
//    config.setName("XPathMonitor");
//    config.setEnabled(true);
//    config.setCron(cron);
//    String xPath = JsoupUtil.getXPathFromAI(url, "zhipu", contentWithoutTimeAndUrl, webMonitorFactory.loadAIModels());
//    config.setXPath(xPath);



    CssSelectorFetcherConfig cssSelectorFetcherConfig = null;
    SimpleFetcherConfig simpleFetcherConfig = null;
    if (url != null) {
      // css
      cssSelectorFetcherConfig = new CssSelectorFetcherConfig();
      cssSelectorFetcherConfig.setUrl(url);
      cssSelectorFetcherConfig.setType("CssSelectorFetcher");
      cssSelectorFetcherConfig.setName("CssMonitor");
      cssSelectorFetcherConfig.setEnabled(true);
      cssSelectorFetcherConfig.setCron(cron);
      String cssSelector = JsoupUtil.getXPathFromAI(url, "zhipu", content, webMonitorFactory.loadAIModels());
      cssSelectorFetcherConfig.setCssSelector(cssSelector);

      List<ObserverConfig> observerConfigs = webMonitorFactory.loadObserverConfigs();
      // 随机生成一个long
      long userId = (long) (Math.random() * 1000000000000000000L);
      boolean b = monitor.startMonitoringByUser(userId, cssSelectorFetcherConfig, observerConfigs, webMonitorFactory.loadAIModels());
      return b;
    } else {
      // simpleFetch
      simpleFetcherConfig = new SimpleFetcherConfig();
      simpleFetcherConfig.setType("SimpleFetcher");
      simpleFetcherConfig.setName("SimpleMonitor");
      simpleFetcherConfig.setEnabled(true);
      simpleFetcherConfig.setContent(content);
      simpleFetcherConfig.setCron(cron);

      List<ObserverConfig> observerConfigs = webMonitorFactory.loadObserverConfigs();
      // 随机生成一个long
      long userId = (long) (Math.random() * 1000000000000000000L);
      boolean b = monitor.startMonitoringByUser(userId, simpleFetcherConfig, observerConfigs, webMonitorFactory.loadAIModels());
      return b;
    }

  }

}