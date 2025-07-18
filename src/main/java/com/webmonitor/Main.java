package com.webmonitor;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.fetcher.FetcherConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebMonitor;
import com.webmonitor.entity.po.TaskUserConfig;
import com.webmonitor.provider.TaskUserConfigProvider;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;


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

  // todo test
  @Resource
  private ToolCallbackProvider tools;

//  public static void main(String[] args) {
//    start();
//  }

  public void start(WebMonitor monitor) {
    log.info("程序启动");

    // todo start ======
    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU));
    var chatClient = builder
            .defaultToolCallbacks(tools)
            .build();

    String userInput = "北京天气";
    System.out.println(" QUESTION: " + userInput);

    ChatClient.CallResponseSpec call = chatClient.prompt(userInput).call();
    ChatResponse chatResponse = call.chatResponse();
    System.out.println(" ASSISTANT: " +
            chatClient.prompt(userInput).call().content());
    // todo end ======

    // 设置允许图形界面，解决java.awt.HeadlessException
    System.setProperty("java.awt.headless", "false");
    // 初始化 Swing 界面
    SwingUtilities.invokeLater(this::createAndShowGUI);
    // 配置 Logback 将日志输出到 Swing 界面
    configureLogbackAppender();


    monitor.startMonitoring(webMonitorFactory.loadFetcherConfigs(),
            webMonitorFactory.loadObserverConfigs(),
            webMonitorFactory.loadAIModels());

    // 从数据库查询有效任务配置并启动监控
    List<TaskUserConfig> configs = taskUserConfigProvider.list(new QueryWrapper<TaskUserConfig>().lambda()
                    .eq(TaskUserConfig::getEnable, true)
            .eq(TaskUserConfig::getDeleted, false));


    log.info("查找到 {} 个有效的任务配置", configs.size());

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