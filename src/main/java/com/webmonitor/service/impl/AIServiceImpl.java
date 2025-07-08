package com.webmonitor.service.impl;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.fetcher.CssSelectorFetcherConfig;
import com.webmonitor.config.fetcher.SimpleFetcherConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.constant.TaskTypeEnum;
import com.webmonitor.constant.WayToGetHtmlEnum;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.provider.TaskUserConfigProvider;
import com.webmonitor.service.AIService;
import com.webmonitor.util.JsoupUtil;
import com.webmonitor.util.UserContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIServiceImpl implements AIService {

  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;
  @Resource
  private TaskUserConfigProvider taskUserConfigProvider;


  @Override
  public boolean setUpTimingTask(String url, String cron, String content) throws Exception {


//    XPathFetcherConfig config = new XPathFetcherConfig();
//    config.setUrl(url);
//    config.setType("XPathFetcher");
//    config.setName("XPathMonitor");
//    config.setEnabled(true);
//    config.setCron(cron);
//    String xPath = JsoupUtil.getXPathFromAI(url, "zhipu", contentWithoutTimeAndUrl, webMonitorFactory.loadAIModels());
//    config.setXPath(xPath);
    List<ObserverConfig> observerConfigs = webMonitorFactory.loadObserverConfigs();

    CssSelectorFetcherConfig cssSelectorFetcherConfig = null;
    SimpleFetcherConfig simpleFetcherConfig = null;
    TaskUserConfig config = new TaskUserConfig();
    config.setUserId(UserContext.getUserId());
    config.setCronExpression(cron);
    config.setWayToGetHtmlCode(WayToGetHtmlEnum.SELENIUM.getCode());
    config.setUrl(url);
    config.setTaskContent(content);
    config.setEnable(true);


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


      // 保存TaskUserConfig到数据库
      config.setTaskTypeCode(TaskTypeEnum.CSS_SELECTOR.getCode());
      config.setCssSelector(cssSelector);
      taskUserConfigProvider.save(config);

      boolean b = monitor.startMonitoringByUser(UserContext.getUserId(), cssSelectorFetcherConfig, observerConfigs, webMonitorFactory.loadAIModels());
      return b;
    } else {
      // simpleFetch
      simpleFetcherConfig = new SimpleFetcherConfig();
      simpleFetcherConfig.setType("SimpleFetcher");
      simpleFetcherConfig.setName("SimpleMonitor");
      simpleFetcherConfig.setEnabled(true);
      simpleFetcherConfig.setContent(content);
      simpleFetcherConfig.setCron(cron);

      // 保存TaskUserConfig到数据库
      config.setTaskTypeCode(TaskTypeEnum.SIMPLE.getCode());
      taskUserConfigProvider.save(config);

      boolean b = monitor.startMonitoringByUser(UserContext.getUserId(), simpleFetcherConfig, observerConfigs, webMonitorFactory.loadAIModels());
      return b;
    }
  }

}
