package com.webmonitor;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.fetcher.FetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.provider.TaskUserConfigProvider;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Slf4j
public class Main {

  private static JTextArea logTextArea;

  @Resource
  private WebMonitorFactory webMonitorFactory;

  @Resource
  private ApplicationContext context;


  @Resource
  private TaskUserConfigProvider taskUserConfigProvider;


//  public static void main(String[] args) {
//    start();
//  }

  public void start(WebMonitor monitor) {
    log.info("程序启动");


    // 设置允许图形界面，解决java.awt.HeadlessException
    System.setProperty("java.awt.headless", "false");
    // 初始化 Swing 界面
    SwingUtilities.invokeLater(this::createAndShowGUI);
    // 配置 Logback 将日志输出到 Swing 界面
    configureLogbackAppender();


    monitor.startMonitoring(webMonitorFactory.loadFetcherConfigs(),
            webMonitorFactory.loadObserverConfigs(),
            webMonitorFactory.loadAIModels());

    // 从数据库查询有效任务配置并启动监控（DBObserver只控制结果是否记录到DB）
    // 先TCP探测数据库是否可达：DB未启动时跳过查询，避免触发Druid连接池后台不停重试刷错误日志
    List<TaskUserConfig> configs = new ArrayList<>();
    if (isDbReachable(context.getEnvironment())) {
      try {
        configs = taskUserConfigProvider.list(new QueryWrapper<TaskUserConfig>().lambda()
                        .eq(TaskUserConfig::getEnable, true)
                .eq(TaskUserConfig::getDeleted, false));
        log.info("查找到 {} 个有效的任务配置", configs.size());
      } catch (Exception e) {
        // 查询失败不中断启动，只是本次不加载已有任务
        log.error("查询任务配置失败，跳过数据库任务加载", e);
      }
    } else {
      log.error("数据库未启动，跳过数据库任务加载");
    }

    for (TaskUserConfig config : configs) {
      try {
        FetcherConfig fetcherConfig = monitor.createFetcherConfigFromTaskConfig(config);
        ContentFetcher contentFetcher = monitor.createContentFetcherFromTaskConfig(config);

        if (fetcherConfig != null && contentFetcher != null) {
          monitor.doStartMonitoring2(config, contentFetcher, fetcherConfig);
          log.info("成功启动任务: {}", config.getTaskContent());
        } else {
          log.error("创建任务配置或抓取器失败，任务ID: {}", config.getId());
        }
      } catch (Exception e) {
        log.error("启动任务失败，任务ID: {}", config.getId(), e);
      }
    }

    // 保持程序运行
    Runtime.getRuntime().addShutdownHook(new Thread(monitor::stop));

    try {
      Thread.currentThread().join();
    } catch (InterruptedException e) {
      monitor.stop();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * TCP探测数据库端口是否可达（不触发Druid连接池），
   * DB未启动时跳过启动期查询，避免Druid后台补连接线程不停重试刷错误日志
   */
  private boolean isDbReachable(Environment environment) {
    String url = environment.getProperty("spring.datasource.druid.url");
    if (url == null) {
      return true;
    }
    Matcher matcher = Pattern.compile("jdbc:mysql://([^:/]+):(\\d+)").matcher(url);
    if (!matcher.find()) {
      return true;
    }
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(matcher.group(1), Integer.parseInt(matcher.group(2))), 2000);
      return true;
    } catch (Exception e) {
      return false;
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