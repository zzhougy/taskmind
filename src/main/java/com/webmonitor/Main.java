package com.webmonitor;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.core.WebMonitor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j
public class Main {

  private static JTextArea logTextArea;

  @Resource
  private WebMonitorFactory webMonitorFactory;

//  public static void main(String[] args) {
//    start();
//  }

  public void start() {
    log.info("程序启动");
    // 设置允许图形界面，解决java.awt.HeadlessException
    System.setProperty("java.awt.headless", "false");
    // 初始化 Swing 界面
    SwingUtilities.invokeLater(this::createAndShowGUI);
    // 配置 Logback 将日志输出到 Swing 界面
    configureLogbackAppender();

    WebMonitor monitor = new WebMonitor();

    // 启动所有监控
    monitor.startAllMonitoring(webMonitorFactory.loadFetcherConfigs(), webMonitorFactory.loadObserverConfigs());

    // 保持程序运行
    Runtime.getRuntime().addShutdownHook(new Thread(monitor::stop));

    try {
      Thread.currentThread().join();
    } catch (InterruptedException e) {
      monitor.stop();
      Thread.currentThread().interrupt();
    }
  }

  private void createAndShowGUI() {
    JFrame frame = new JFrame("实时日志监控");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);

    logTextArea = new JTextArea();
    logTextArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(logTextArea);

    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    frame.setVisible(true);
  }

  private void configureLogbackAppender() {
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