package com.webmonitor;

import com.webmonitor.core.WebMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WebMonitorApplication {
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(WebMonitorApplication.class, args);
    System.out.println("[====================] 100% 系统启动成功");
    System.out.println(" 🚀 服务已启动，监听端口...");


    WebMonitorApplication application = new WebMonitorApplication();
    application.init(context);
  }

  public void init(ApplicationContext context) {
    System.out.println("✅ 任务开始执行。。。。。");

    Main main = context.getBean(Main.class);
    WebMonitor webMonitor = context.getBean(WebMonitor.class);
    main.start(webMonitor);

  }
}
