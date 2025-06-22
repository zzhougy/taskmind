package com.webmonitor.core;

import com.webmonitor.config.fetcher.CssSelectorFetcherConfig;
import com.webmonitor.config.fetcher.FetcherConfig;
import com.webmonitor.config.fetcher.SeleniumFetcherConfig;
import com.webmonitor.config.fetcher.ZzFetcherConfig;
import com.webmonitor.config.observer.ConsoleObserverConfig;
import com.webmonitor.config.observer.EmailObserverConfig;
import com.webmonitor.config.observer.ObserverConfig;
import com.webmonitor.fetcher.CssSelectorFetcher;
import com.webmonitor.fetcher.SeleniumFetcher;
import com.webmonitor.fetcher.ZzFetcher;
import com.webmonitor.observer.ConsoleWebObserver;
import com.webmonitor.observer.EmailWebObserver;
import com.webmonitor.observer.WebObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public void startMonitoring(FetcherConfig fetcherConfig) {
        ContentFetcher fetcher;
        if (fetcherConfig instanceof ZzFetcherConfig) {
            fetcher = new ZzFetcher((ZzFetcherConfig) fetcherConfig);
        } else if (fetcherConfig instanceof CssSelectorFetcherConfig) {
            fetcher = new CssSelectorFetcher((CssSelectorFetcherConfig) fetcherConfig);
        } else if (fetcherConfig instanceof SeleniumFetcherConfig) {
            fetcher = new SeleniumFetcher((SeleniumFetcherConfig) fetcherConfig);
        } else {
            fetcher = null;
        }
        if (fetcher == null) {
            log.error("未找到名为 {} 的内容获取器", fetcherConfig.getName());
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<WebContent> webContents = fetcher.fetch();
                if (webContents != null && !webContents.isEmpty()) {
                    notifyObservers(webContents);
                }
            } catch (Exception e) {
                log.error("监控任务执行失败: {}", fetcherConfig.getName(), e);
            }
        }, 0, fetcherConfig.getIntervalSeconds(), TimeUnit.SECONDS);
    }

    public void startAllMonitoring(List<FetcherConfig> fetcherConfigs, List<ObserverConfig> observerConfigs) {
        fetcherConfigs.forEach(o->{
            if (o.isEnabled()) {
                startMonitoring(o);
            }
        });

        observerConfigs.forEach(o->{
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