package com.webmonitor;

import com.webmonitor.core.WebMonitor;
import com.webmonitor.observer.ConsoleWebObserver;
import com.webmonitor.observer.EmailWebObserver;
import com.webmonitor.observer.QyWeixinWebObserver;

public class Main {
    public static void main(String[] args) {
        WebMonitor monitor = new WebMonitor();
        
        // 注册内容获取器
        monitor.registerContentFetcher(WebMonitorEnum.Zz);

        // 添加观察者
        monitor.addObserver(new ConsoleWebObserver());
        monitor.addObserver(new QyWeixinWebObserver());
        monitor.addObserver(new EmailWebObserver());

        // 启动所有监控
        monitor.startAllMonitoring();
        
        // 保持程序运行
        Runtime.getRuntime().addShutdownHook(new Thread(monitor::stop));
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            monitor.stop();
            Thread.currentThread().interrupt();
        }
    }
} 