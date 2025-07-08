package com.webmonitor.core;

import com.webmonitor.config.fetcher.*;
import com.webmonitor.config.observer.ConsoleObserverConfig;
import com.webmonitor.config.observer.EmailObserverConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.constant.TaskTypeEnum;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.provider.TaskUserConfigProvider;
import com.webmonitor.service.fetcher.*;
import com.webmonitor.service.job.UserSchedulerService;
import com.webmonitor.service.observer.ConsoleWebObserver;
import com.webmonitor.service.observer.EmailWebObserver;
import com.webmonitor.service.observer.WebObserver;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class WebMonitor {
  private final List<WebObserver> observers = new CopyOnWriteArrayList<>();
  private final ScheduledExecutorService scheduler;
  @Resource
  private UserSchedulerService schedulerService;
  @Resource
  private TaskUserConfigProvider taskUserConfigProvider;

  public WebMonitor() {
    this.scheduler = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            }
    );
  }

  // 根据TaskUserConfig创建FetcherConfig
  public FetcherConfig createFetcherConfigFromTaskConfig(TaskUserConfig config) {
    if (config == null) {
      log.error("任务配置为空");
      return null;
    }

    // 根据taskTypeCode创建对应的FetcherConfig
    FetcherConfig fetcherConfig = null;
    TaskTypeEnum taskTypeEnum = TaskTypeEnum.valueOf(config.getTaskTypeCode());
    switch (taskTypeEnum) {
      case CSS_SELECTOR:
        CssSelectorFetcherConfig cssConfig = new CssSelectorFetcherConfig();
        cssConfig.setUrl(config.getUrl());
        cssConfig.setCron(config.getCronExpression());
        cssConfig.setEnabled(true);
        cssConfig.setCssSelector(config.getCssSelector());
        fetcherConfig = cssConfig;
        break;
      case XPATH_SELECTOR:
        XPathFetcherConfig xpathConfig = new XPathFetcherConfig();
        xpathConfig.setUrl(config.getUrl());
        xpathConfig.setCron(config.getCronExpression());
        xpathConfig.setEnabled(true);
        xpathConfig.setXPath(config.getXpathSelector());
        fetcherConfig = xpathConfig;
        break;
      case SIMPLE:
        SimpleFetcherConfig simpleConfig = new SimpleFetcherConfig();
        simpleConfig.setUrl(config.getUrl());
        simpleConfig.setCron(config.getCronExpression());
        simpleConfig.setEnabled(true);
        simpleConfig.setContent(config.getTaskContent());
        fetcherConfig = simpleConfig;
        break;
      default:
        log.error("不支持的任务类型: {}", config.getTaskTypeCode());
    }

    return fetcherConfig;
  }

  // 根据TaskUserConfig创建ContentFetcher
  public ContentFetcher createContentFetcherFromTaskConfig(TaskUserConfig config) {
    if (config == null) {
      log.error("任务配置为空");
      return null;
    }

    // 根据wayToGetHtmlCode创建对应的ContentFetcher
    ContentFetcher fetcher = null;
    FetcherConfig fetcherConfig = createFetcherConfigFromTaskConfig(config);
    if (fetcherConfig == null) {
      return null;
    }

    switch (config.getWayToGetHtmlCode()) {
      case "SIMPLE":
        fetcher = new SimpleFetcher((SimpleFetcherConfig) fetcherConfig);
        break;
      case "SELENIUM":
        fetcher = new SeleniumFetcher((SeleniumFetcherConfig) fetcherConfig);
        break;
      case "ZZ":
        fetcher = new ZzFetcher((ZzFetcherConfig) fetcherConfig);
        break;
      default:
        log.error("不支持的HTML获取方式: {}", config.getWayToGetHtmlCode());
    }

    return fetcher;
  }

  public void addObserver(WebObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(WebObserver observer) {
    observers.remove(observer);
  }

  public void doStartMonitoring(ContentFetcher fetcher, FetcherConfig fetcherConfig) {
    AtomicReference<Future<?>> futureRef = new AtomicReference<>();
    // scheduleAtFixedRate
    // scheduleWithFixedDelay: 任务执行完后，等待IntervalSeconds，再继续重复执行当前任务
    futureRef.set(scheduler.scheduleWithFixedDelay(() -> {
      try {
        List<WebContent> webContents = fetcher.fetch();
        if (webContents != null && !webContents.isEmpty()) {
          notifyObservers(webContents);
        }
      } catch (Exception e) {
        log.error("监控任务执行失败: {}", fetcherConfig.getName(), e);
        // 取消当前任务，不影响其他任务
        futureRef.get().cancel(true);
      }
    }, 0, fetcherConfig.getIntervalSeconds(), TimeUnit.SECONDS));
  }



  public void doStartMonitoring2(Long userId, ContentFetcher fetcher, FetcherConfig fetcherConfig) {
    schedulerService.scheduleTaskForUser(userId,
            fetcherConfig.getCron(), createUserTask(userId, fetcher));
  }

  public boolean startMonitoringByUser(Long userId, FetcherConfig fetcherConfig, List<ObserverConfig> observerConfigs,  Map<AIModelEnum, ChatModel> aiModelMap) {
    observerConfigs.forEach(this::doMonitorConfig);
    return doFetcherConfig(userId, aiModelMap, fetcherConfig);
  }

  public void startMonitoring(List<FetcherConfig> fetcherConfigs, List<ObserverConfig> observerConfigs,  Map<AIModelEnum, ChatModel> aiModelMap) {
    observerConfigs.forEach(this::doMonitorConfig);

    fetcherConfigs.forEach(fetcherConfig -> {
      doFetcherConfig(null, aiModelMap, fetcherConfig);
    });
  }

  private boolean doFetcherConfig(Long userId, Map<AIModelEnum, ChatModel> aiModelMap, FetcherConfig fetcherConfig) {
    if (fetcherConfig.isEnabled()) {
      ContentFetcher fetcher;
      if (fetcherConfig instanceof ZzFetcherConfig) {
        fetcher = new ZzFetcher((ZzFetcherConfig) fetcherConfig);
      } else if (fetcherConfig instanceof CssSelectorFetcherConfig) {
        fetcher = new CssSelectorFetcher((CssSelectorFetcherConfig) fetcherConfig);
      } else if (fetcherConfig instanceof XPathFetcherConfig) {
        fetcher = new XPathFetcher((XPathFetcherConfig) fetcherConfig);
      } else if (fetcherConfig instanceof SeleniumFetcherConfig) {
        fetcher = new SeleniumFetcher((SeleniumFetcherConfig) fetcherConfig);
      } else if (fetcherConfig instanceof KeywordSelectorFetcherConfig) {
        fetcher = new KeywordSelectorFetcher((KeywordSelectorFetcherConfig) fetcherConfig);
      } else if (fetcherConfig instanceof AIFetcherConfig) {
        fetcher = new AIFetcher((AIFetcherConfig) fetcherConfig, aiModelMap);
      }  else if (fetcherConfig instanceof SimpleFetcherConfig) {
        fetcher = new SimpleFetcher((SimpleFetcherConfig) fetcherConfig);
      } else {
        fetcher = null;
      }
      if (fetcher == null) {
        log.error("未找到名为 {} 的内容获取器", fetcherConfig.getName());
        return false;
      }
      if (userId != null) {
        try {
          fetcher.fetch() ;
        } catch (Exception e) {
          log.error("任务测试执行时出现异常，请重试，用户 " + userId);
          return false;
        }
        doStartMonitoring2(userId, fetcher, fetcherConfig);
      } else {
        doStartMonitoring(fetcher, fetcherConfig);
      }
    }
    return true;
  }

  private void doMonitorConfig(ObserverConfig o) {
    if (o.isEnabled()) {
      WebObserver observer = null;
      if (o instanceof ConsoleObserverConfig) {
        observer = new ConsoleWebObserver();
        addObserver(observer);
      }
      if (o instanceof EmailObserverConfig) {
        observer = new EmailWebObserver((EmailObserverConfig) o);
        addObserver(observer);
      }
    }
  }

  public void stop() {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  private void notifyObservers(List<WebContent> webContents) {
    // 通知观察者
    for (WebObserver observer : observers) {
      try {
        observer.send(webContents);
      } catch (Exception e) {
        log.error("通知观察者失败", e);
      }
    }
  }



  public Runnable createUserTask(Long userId, ContentFetcher fetcher) {
    return () -> {
      try {
        List<WebContent> webContents = fetcher.fetch();

        if (webContents != null && !webContents.isEmpty()) {
          notifyObservers(webContents);
        }
      } catch (Exception e) {
        // 错误处理 - 取消该用户的任务
        schedulerService.cancelTaskForUser(userId);
        log.error("任务移除，用户 " + userId + " 的任务执行失败: " + e.getMessage());
      }
    };
  }
}