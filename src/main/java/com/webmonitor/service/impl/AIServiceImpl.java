package com.webmonitor.service.impl;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.exception.BusinessException;
import com.webmonitor.config.exception.SystemException;
import com.webmonitor.config.fetcher.FetcherConfig;
import com.webmonitor.config.fetcher.SimpleFetcherConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.constant.ErrorCodeEnum;
import com.webmonitor.constant.TaskTypeEnum;
import com.webmonitor.constant.WayToGetHtmlEnum;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.bo.AIUserInputBO;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.provider.TaskUserConfigProvider;
import com.webmonitor.service.AIService;
import com.webmonitor.service.springai.TaskAssignTools;
import com.webmonitor.service.springai.TaskForWebMonitorTools;
import com.webmonitor.service.springai.TaskForAIMcpTools;
import com.webmonitor.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.webmonitor.service.springai.TaskForAIMcpTools.TASK_SETTING_ERROR;
import static com.webmonitor.service.springai.TaskForAIMcpTools.TASK_SETTING_SUCCESS2;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

  public static final int INT = 60 * 60;
  public static final AIModelEnum MODEL_FOR_SET_UP_TIMING_TASK = AIModelEnum.ZHIPU_GLM4_FLASH;
  public static final String TASK_HANDLE_SUCCESS = "任务执行完成，请前往查看结果";
  @Resource
  private WebMonitor monitor;
  @Resource
  private WebMonitorFactory webMonitorFactory;
  @Resource
  private TaskUserConfigProvider taskUserConfigProvider;

  private final AtomicInteger queueCount = new AtomicInteger(0);
  private static final int MAX_QUEUE_SIZE = 3;
  @Resource
  private TaskForAIMcpTools taskForAIMcpTools;
  @Resource
  private TaskAssignTools taskAssignTools;
  @Resource
  private TaskForWebMonitorTools taskForWebMonitorTools;

  @Resource
  @Lazy
  private AIService self;

  @Transactional
  @Override
  public void setUpTimingTask(String userInput, boolean isNeedNetData, String cron, String target) throws Exception {

    TaskUserConfig config = new TaskUserConfig();
    config.setUserId(UserContext.getUserId());
    config.setCronExpression(cron);
    config.setUrl(null);
    config.setTaskContent(target);
    config.setEnable(true);
    config.setUserInput(userInput);


    if (isNeedNetData) {
      /**
       * 1、xpath
       */
//    if (CronUtil.getIntervalInSeconds(cron) < INT && CronUtil.getIntervalInSeconds(cron) > 0) {
//      throw new BusinessException(ErrorCodeEnum.AI_TASK_INTERVAL_TOO_SHORT.getMsg());
//    }

//    XPathFetcherConfig config = new XPathFetcherConfig();
//    config.setUrl(url);
//    config.setType("XPathFetcher");
//    config.setName("XPathMonitor");
//    config.setEnabled(true);
//    config.setCron(cron);
//    String xPath = JsoupUtil.getXPathFromAI(url, "zhipu", contentWithoutTimeAndUrl, webMonitorFactory.loadAIModels());
//    config.setXPath(xPath);



      /**
       * 2、css
       */
//      CssSelectorFetcherConfig cssSelectorFetcherConfig = null;
//      cssSelectorFetcherConfig = new CssSelectorFetcherConfig();
//      cssSelectorFetcherConfig.setUrl(url);
//      cssSelectorFetcherConfig.setType("CssSelectorFetcher");
//      cssSelectorFetcherConfig.setName("CssMonitor");
//      cssSelectorFetcherConfig.setEnabled(true);
//      cssSelectorFetcherConfig.setCron(cron);
//      cssSelectorFetcherConfig.setWayToGetHtml(WAY_TO_GET_HTML.getCode());
//
//      Document document = HtmlUtil.getDocumentByWayToGetHtml(url, WAY_TO_GET_HTML);
//      String cleanedHtml = HtmlUtil.cleanHtml(document.html());
//      List<String> split = AIUtil.getKeywordsFromAIByOutputConverter(cleanedHtml, MODEL.getName(),
//              target, webMonitorFactory.loadAIModels());
//      log.info("=== 关键词：{}", split);
//      Map<String, String> stringStringHashMap = new HashMap<>();
//      for (String s : split) {
//        stringStringHashMap.put(s, s);
//      }
//      Map<String, String> stringStringHashMap1 = new LinkedHashMap<>();
//      for (String value : stringStringHashMap.values()) {
//        Element contentDocumentByKeyWord = JsoupUtil.getContentDocumentByKeyWord(document, value);
//        String cssSelector = contentDocumentByKeyWord.cssSelector();
////      String cssSelector = JsoupUtil.getXPathFromAI(url, "zhipu", target, webMonitorFactory.loadAIModels());
//        stringStringHashMap1.put(cssSelector, cssSelector + "|text");
//      }
//      cssSelectorFetcherConfig.setCssSelectors(stringStringHashMap1);
//
//
//      config.setWayToGetHtmlCode(WAY_TO_GET_HTML.getCode());
//      config.setTaskTypeCode(TaskTypeEnum.CSS_SELECTOR.getCode());
//      config.setCssSelectors(stringStringHashMap1.values().stream().toList());
//      config.setKeywords(stringStringHashMap.values().stream().toList());
//      taskUserConfigProvider.save(config);
//      boolean b = monitor.startMonitoringByUser(config, cssSelectorFetcherConfig, webMonitorFactory.loadObserverConfigs(), webMonitorFactory.loadAIModels());
//      if (!b) {
//        throw new SystemException("任务启动失败");
//      }


      /**
       * 3、mcp_playwright
       */
      config.setTaskTypeCode(TaskTypeEnum.AI_MCP.getCode());
      taskUserConfigProvider.save(config);
      FetcherConfig mcpFetcherConfig = monitor.createFetcherConfigFromTaskConfig(config);
      boolean b = monitor.startMonitoringByUser(config, mcpFetcherConfig, webMonitorFactory.loadObserverConfigs(), webMonitorFactory.loadAIModels());
      if (!b) {
        throw new SystemException("任务启动失败");
      }

    } else {
      // simpleFetch
      SimpleFetcherConfig simpleFetcherConfig = new SimpleFetcherConfig();
      simpleFetcherConfig.setType("SimpleFetcher");
      simpleFetcherConfig.setName("SimpleMonitor");
      simpleFetcherConfig.setEnabled(true);
      simpleFetcherConfig.setContent(target);
      simpleFetcherConfig.setCron(cron);

      // 保存TaskUserConfig到数据库
      config.setTaskTypeCode(TaskTypeEnum.SIMPLE.getCode());
      taskUserConfigProvider.save(config);

      boolean b = monitor.startMonitoringByUser(config, simpleFetcherConfig, webMonitorFactory.loadObserverConfigs(), webMonitorFactory.loadAIModels());
      if (!b) {
        throw new SystemException("任务启动失败");
      }
    }
  }

  @Override
  public String chatWithAIMcp(AIUserInputBO bo) {

    // 校验敏感词
//    String filteredContent = SensitiveUtil.filterSensitiveWords(bo.getUserInput());
//    if (filteredContent.contains("*")) {
//      return "请勿输入敏感词";
//    }

    int currentCount = queueCount.incrementAndGet();
    if (currentCount > MAX_QUEUE_SIZE) {
      queueCount.decrementAndGet();
      log.error("Queue size exceeds maximum limit of " + MAX_QUEUE_SIZE);
      throw new BusinessException("排队人数较多，请稍后再试");
    }
    try {
      synchronized (this) {
        String prompt = bo.getUserInput();
        ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(MODEL_FOR_SET_UP_TIMING_TASK))
                .prompt(prompt)
                .tools(taskForAIMcpTools)
                .toolContext(Map.of("userInput", bo.getUserInput(), "cron", bo.getCron()))
                .call();

        ChatClientResponse chatClientResponse = call.chatClientResponse();
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        Generation result = chatResponse.getResult();
        String content = result.getOutput().getText();
        log.info("AI Response: {}", content);
        // 没有用工具直接立马执行任务
        ChatGenerationMetadata metadata = result.getMetadata();
        if (metadata.isEmpty()) {

          self.setUpTimingTask(bo.getUserInput(), true, "0 0 0 1 1 ? 2022", bo.getUserInput());

          return TASK_HANDLE_SUCCESS;
        }

        if (!TASK_SETTING_SUCCESS2.equals(content) || ErrorCodeEnum.AI_TASK_INTERVAL_TOO_SHORT.getMsg().equals(content)) {
          return TASK_SETTING_ERROR;
        }
        return content;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      queueCount.decrementAndGet();
    }
  }


  @Override
  public String chatWithAIEntrance(AIUserInputBO bo) {

    // 校验敏感词
//    String filteredContent = SensitiveUtil.filterSensitiveWords(bo.getUserInput());
//    if (filteredContent.contains("*")) {
//      return "请勿输入敏感词";
//    }

    int currentCount = queueCount.incrementAndGet();
    if (currentCount > MAX_QUEUE_SIZE) {
      queueCount.decrementAndGet();
      log.error("Queue size exceeds maximum limit of " + MAX_QUEUE_SIZE);
      throw new BusinessException("排队人数较多，请稍后再试");
    }
    try {
      synchronized (this) {
        String prompt = "使用tool分析用户输入：" + bo.getUserInput();
        ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(MODEL_FOR_SET_UP_TIMING_TASK))
                .prompt(prompt)
                .tools(taskAssignTools)
                .toolContext(Map.of("userInput", bo.getUserInput()))
                .call();

        ChatClientResponse chatClientResponse = call.chatClientResponse();
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        Generation result = chatResponse.getResult();
        String content = result.getOutput().getText();
        log.info("AI Response: {}", content);

        if (!TASK_SETTING_SUCCESS2.equals(content) || ErrorCodeEnum.AI_TASK_INTERVAL_TOO_SHORT.getMsg().equals(content)) {
          return TASK_SETTING_ERROR;
        }
        return content;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      queueCount.decrementAndGet();
    }
  }

  @Override
  public String chatWithAIForMonitor(AIUserInputBO bo) {

    // 校验敏感词
//    String filteredContent = SensitiveUtil.filterSensitiveWords(bo.getUserInput());
//    if (filteredContent.contains("*")) {
//      return "请勿输入敏感词";
//    }

    int currentCount = queueCount.incrementAndGet();
    if (currentCount > MAX_QUEUE_SIZE) {
      queueCount.decrementAndGet();
      log.error("Queue size exceeds maximum limit of " + MAX_QUEUE_SIZE);
      throw new BusinessException("排队人数较多，请稍后再试");
    }
    try {
      synchronized (this) {
        String prompt = bo.getUserInput();
        ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(MODEL_FOR_SET_UP_TIMING_TASK))
                .prompt(prompt)
                .tools(taskForWebMonitorTools)
                .toolContext(Map.of("userInput", bo.getUserInput(), "cron", bo.getCron()))
                .call();

        ChatClientResponse chatClientResponse = call.chatClientResponse();
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        Generation result = chatResponse.getResult();
        String content = result.getOutput().getText();
        log.info("AI Response: {}", content);

        if (!TASK_SETTING_SUCCESS2.equals(content) || ErrorCodeEnum.AI_TASK_INTERVAL_TOO_SHORT.getMsg().equals(content)) {
          return TASK_SETTING_ERROR;
        }
        return content;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      queueCount.decrementAndGet();
    }
  }

  @Transactional
  @Override
  public String setUpTimingTaskWebMonitor(String cron, String url, String cssSelector, String xPath) throws Exception {
    TaskUserConfig config = new TaskUserConfig();
    config.setUserId(UserContext.getUserId());
    config.setCronExpression(cron);
    config.setUrl(url);
    config.setTaskContent("target");
    config.setEnable(true);
    config.setUserInput("userInput");
    config.setCssSelectors(List.of(cssSelector + "|text"));
    config.setXpathSelector(StringUtils.hasText(xPath) ? (xPath + "|text") : null);
    config.setTaskTypeCode(StringUtils.hasText(cssSelector) ? TaskTypeEnum.CSS_SELECTOR.getCode() : TaskTypeEnum.XPATH_SELECTOR.getCode());

    /**
     * 先尝试jsoup获取
     */
    config.setWayToGetHtmlCode(WayToGetHtmlEnum.JSOUP.getCode());
    taskUserConfigProvider.save(config);
    FetcherConfig mcpFetcherConfig = monitor.createFetcherConfigFromTaskConfig(config);
    boolean b = monitor.startMonitoringByUser(config, mcpFetcherConfig, webMonitorFactory.loadObserverConfigs(), webMonitorFactory.loadAIModels());
    if (!b) {
      // 尝试play获取
      config.setWayToGetHtmlCode(WayToGetHtmlEnum.PLAYWRIGHT.getCode());
      taskUserConfigProvider.updateTaskUserConfigById(config);
      mcpFetcherConfig = monitor.createFetcherConfigFromTaskConfig(config);
      boolean b2 = monitor.startMonitoringByUser(config, mcpFetcherConfig, webMonitorFactory.loadObserverConfigs(), webMonitorFactory.loadAIModels());
      if (!b2) {
        throw new SystemException("任务启动失败");
      }
    }


    return "";
  }
}
