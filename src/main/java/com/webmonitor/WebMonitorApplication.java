package com.webmonitor;

import com.webmonitor.core.WebMonitor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.autotable.springboot.EnableAutoTable;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
@MapperScan(basePackages = {"com.webmonitor.mapper"})
@EnableAutoTable // 声明使用AutoTable框架
public class WebMonitorApplication {
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(WebMonitorApplication.class, args);
    log.info("[====================] 100% 系统启动成功");
    log.info(" 🚀 服务已启动，监听端口...");


    WebMonitorApplication application = new WebMonitorApplication();
    application.init(context);
  }

  public void init(ApplicationContext context) {
    log.info("✅ 任务开始执行。。。。。");

    Main main = context.getBean(Main.class);
    WebMonitor webMonitor = context.getBean(WebMonitor.class);
    main.start(webMonitor);

  }
}
