package com.webmonitor;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.observer.ConsoleWebObserver;
import com.webmonitor.observer.SlackWebObserver;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class Main {

  private static JTextArea logTextArea;

  public static void main(String[] args) {

    log.info("程序启动");
    // 初始化 Swing 界面
    SwingUtilities.invokeLater(Main::createAndShowGUI);
    // 配置 Logback 将日志输出到 Swing 界面
    configureLogbackAppender();

    WebMonitor monitor = new WebMonitor();

    // 注册内容获取器
    monitor.registerContentFetcher(WebMonitorEnum.Zz);

    // 添加观察者
    monitor.addObserver(new ConsoleWebObserver());
//        monitor.addObserver(new QyWeixinWebObserver());
//        monitor.addObserver(new EmailWebObserver());
    monitor.addObserver(new SlackWebObserver());

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

  private static void createAndShowGUI() {
    JFrame frame = new JFrame("实时日志监控");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);

    logTextArea = new JTextArea();
    logTextArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(logTextArea);

    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    frame.setVisible(true);
  }

  private static void configureLogbackAppender() {
    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(rootLogger.getLoggerContext());
    encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{20} - %msg%n");
    encoder.start();

    AppenderBase<ILoggingEvent> swingAppender = new AppenderBase<ILoggingEvent>() {
      @Override
      protected void append(ILoggingEvent eventObject) {
        String logMessage = encoder.getLayout().doLayout(eventObject);
        SwingUtilities.invokeLater(() -> logTextArea.append(logMessage));
      }
    };
    swingAppender.setContext(rootLogger.getLoggerContext());
    swingAppender.start();

    rootLogger.addAppender(swingAppender);
  }
} 