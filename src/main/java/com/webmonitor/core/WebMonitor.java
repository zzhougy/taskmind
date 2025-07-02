package com.webmonitor.core;

import com.webmonitor.config.fetcher.*;
import com.webmonitor.config.observer.ConsoleObserverConfig;
import com.webmonitor.config.observer.EmailObserverConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.fetcher.*;
import com.webmonitor.observer.ConsoleWebObserver;
import com.webmonitor.observer.EmailWebObserver;
import com.webmonitor.observer.WebObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class WebMonitor {
  private final List<WebObserver> observers = new CopyOnWriteArrayList<>();
  private final ScheduledExecutorService scheduler;

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

  public void addObserver(WebObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(WebObserver observer) {
    observers.remove(observer);
  }

  public void startMonitoring(FetcherConfig fetcherConfig, Map<AIModelEnum, ChatModel> aiModelMap) {
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
    } else {
      fetcher = null;
    }
    if (fetcher == null) {
      log.error("未找到名为 {} 的内容获取器", fetcherConfig.getName());
      return;
    }

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

  public void startAllMonitoring(List<FetcherConfig> fetcherConfigs, List<ObserverConfig> observerConfigs,  Map<AIModelEnum, ChatModel> aiModelMap) {
    fetcherConfigs.forEach(o -> {
      if (o.isEnabled()) {
        startMonitoring(o, aiModelMap);
      }
    });

    observerConfigs.forEach(o -> {
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
    });
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
    for (WebObserver observer : observers) {
      try {
        observer.send(webContents);
      } catch (Exception e) {
        log.error("通知观察者失败", e);
      }
    }
  }
}