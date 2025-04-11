package com.webmonitor.core;

import com.webmonitor.WebMonitorEnum;
import com.webmonitor.observer.WebObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebMonitor {
    private final List<WebMonitorEnum> webMonitorEnums = new ArrayList<>();
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

    public void registerContentFetcher(WebMonitorEnum webMonitorEnum) {
        webMonitorEnums.add(webMonitorEnum);
    }

    public void addObserver(WebObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WebObserver observer) {
        observers.remove(observer);
    }

    public void startMonitoring(WebMonitorEnum webMonitorEnum) {
        ContentFetcher fetcher = webMonitorEnum.getContentFetcher();
        if (fetcher == null) {
            log.error("未找到名为 {} 的内容获取器", webMonitorEnum.getName());
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<WebContent> webContents = fetcher.fetch();
                if (webContents != null && !webContents.isEmpty()) {
                    notifyObservers(webContents);
                }
            } catch (Exception e) {
                log.error("监控任务执行失败: {}", webMonitorEnum.getName(), e);
            }
        }, 0, webMonitorEnum.getIntervalSeconds(), TimeUnit.SECONDS);
    }

    public void startAllMonitoring() {
        webMonitorEnums.forEach(this::startMonitoring);
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
                observer.update(webContents);
            } catch (Exception e) {
                log.error("通知观察者失败", e);
            }
        }
    }
} 