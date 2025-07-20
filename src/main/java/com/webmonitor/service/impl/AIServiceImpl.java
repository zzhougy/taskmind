package com.webmonitor.service.impl;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.config.exception.SystemException;
import com.webmonitor.config.fetcher.CssSelectorFetcherConfig;
import com.webmonitor.config.fetcher.SimpleFetcherConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.constant.ErrorCodeEnum;
import com.webmonitor.constant.TaskTypeEnum;
import com.webmonitor.constant.WayToGetHtmlEnum;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.bo.AIUserInputBO;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.provider.TaskUserConfigProvider;
import com.webmonitor.service.AIService;
import com.webmonitor.service.springai.TaskTools;
import com.webmonitor.util.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.webmonitor.service.springai.TaskTools.TASK_SETTING_ERROR;
import static com.webmonitor.service.springai.TaskTools.TASK_SETTING_SUCCESS2;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

  public static final int INT = 60 * 60;
  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;
  @Resource
  private TaskUserConfigProvider taskUserConfigProvider;

  private final AtomicInteger queueCount = new AtomicInteger(0);
  private static final int MAX_QUEUE_SIZE = 3;
  @Resource
  private TaskTools taskTools;

  @Transactional
  @Override
  public void setUpTimingTask(String userInput, String url, String cron, String target) throws Exception {
    if (CronUtil.getIntervalInSeconds(cron) < INT && CronUtil.getIntervalInSeconds(cron) > 0) {
      throw new BusinessException(ErrorCodeEnum.AI_TASK_INTERVAL_TOO_SHORT.getMsg());
    }

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
    config.setUrl(url);
    config.setTaskContent(target);
    config.setEnable(true);
    config.setUserInput(userInput);


    if (url != null) {
      // css
      cssSelectorFetcherConfig = new CssSelectorFetcherConfig();
      cssSelectorFetcherConfig.setUrl(url);
      cssSelectorFetcherConfig.setType("CssSelectorFetcher");
      cssSelectorFetcherConfig.setName("CssMonitor");
      cssSelectorFetcherConfig.setEnabled(true);
      cssSelectorFetcherConfig.setCron(cron);
      cssSelectorFetcherConfig.setWayToGetHtml(WayToGetHtmlEnum.SELENIUM.getCode());

      Document document = HtmlUtil.getDocumentByWayToGetHtml(url, WayToGetHtmlEnum.SELENIUM);
      String cleanedHtml = HtmlUtil.cleanHtml(document.html());
      String keyword = AIUtil.getKeywordFromAI(cleanedHtml, "zhipu", target, webMonitorFactory.loadAIModels());
      log.info("ai返回的关键词：{}", keyword);
      keyword = keyword.replace("`",   "");
      keyword = keyword.replace("xpath",   "");
      // 去掉换行
      keyword = keyword.replace("\n", "");
      log.info("处理通过ai获取关键词后: {}", keyword);
      Element contentDocumentByKeyWord = JsoupUtil.getContentDocumentByKeyWord(document, keyword);
      String cssSelector = contentDocumentByKeyWord.cssSelector();
//      String cssSelector = JsoupUtil.getXPathFromAI(url, "zhipu", target, webMonitorFactory.loadAIModels());
      cssSelectorFetcherConfig.setCssSelector(cssSelector + "|text");

      config.setWayToGetHtmlCode(WayToGetHtmlEnum.SELENIUM.getCode());
      config.setTaskTypeCode(TaskTypeEnum.CSS_SELECTOR.getCode());
      config.setCssSelector(cssSelector + "|text");
      taskUserConfigProvider.save(config);
      boolean b = monitor.startMonitoringByUser(config, cssSelectorFetcherConfig, observerConfigs, webMonitorFactory.loadAIModels());
      if (!b) {
        throw new SystemException("任务启动失败");
      }
    } else {
      // simpleFetch
      simpleFetcherConfig = new SimpleFetcherConfig();
      simpleFetcherConfig.setType("SimpleFetcher");
      simpleFetcherConfig.setName("SimpleMonitor");
      simpleFetcherConfig.setEnabled(true);
      simpleFetcherConfig.setContent(target);
      simpleFetcherConfig.setCron(cron);

      // 保存TaskUserConfig到数据库
      config.setTaskTypeCode(TaskTypeEnum.SIMPLE.getCode());
      taskUserConfigProvider.save(config);

      boolean b = monitor.startMonitoringByUser(config, simpleFetcherConfig, observerConfigs, webMonitorFactory.loadAIModels());
      if (!b) {
        throw new SystemException("任务启动失败");
      }
    }
  }

  @Override
  public String chatWithAI(AIUserInputBO bo) {

    // 校验敏感词
    String filteredContent = SensitiveUtil.filterSensitiveWords(bo.getUserInput());
    if (filteredContent.contains("*")) {
      return "请勿输入敏感词";
    }

    int currentCount = queueCount.incrementAndGet();
    if (currentCount > MAX_QUEUE_SIZE) {
      queueCount.decrementAndGet();
      log.error("Queue size exceeds maximum limit of " + MAX_QUEUE_SIZE);
      throw new BusinessException("排队人数较多，请稍后再试");
    }
    try {
      synchronized (this) {
        // todo
        String prompt = "你需要判断是否需要知道当前时间用于设置cron定时任务，获取到当前时间之后才去设置cron定时任务，以下是输入：" + bo.getUserInput();
//        prompt = bo.getUserInput();
        ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU))
                .prompt(prompt)
                .tools(taskTools)
                .toolContext(Map.of("userInput", bo.getUserInput()))
                .call();

        String content = call.content();
        log.info("AI Response: {}", content);
        if (!TASK_SETTING_SUCCESS2.equals(content) || ErrorCodeEnum.AI_TASK_INTERVAL_TOO_SHORT.getMsg().equals(content)) {
          return TASK_SETTING_ERROR;
        }
        return content;
      }
    } finally {
      queueCount.decrementAndGet();
    }
  }

}
