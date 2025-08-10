# ☕️ taskmind-一句话就能指挥它定时完成自动化网页操作

🌐网站🤖自动化👀监控🚨通知
**像大脑一样完成你的任务，你可以把它当做你的私人助理，指挥它什么时候去执行任务，并在完成后提醒你**

## 功能特性

- 网站内容监控工具，支持监控多个网站的内容更新（webMonitor）
- 灵活的内容解析机制（CSS选择器、XPath、AI分析等）
- 支持多种通知方式（邮件、Slack、企业微信）
- 支持自定义内容过滤规则
- AI辅助内容分析（基于Spring AI）

## 效果演示

<img width="200" height="300" alt="Image" content="聊天" src="https://github.com/user-attachments/assets/baf92a41-5fae-4058-a64e-97e47f8e673e" />
<img width="200" height="300" alt="Image" content="任务管理" src="https://github.com/user-attachments/assets/23f66d7c-8a5b-47c6-8fef-758ddca233c4" />
<img width="200" height="300" alt="Image" content="任务结果" src="https://github.com/user-attachments/assets/817e300e-5215-4934-adee-03f9fdac98a9" />
<img width="200" height="300" alt="Image" content="美团微博" src="https://github.com/user-attachments/assets/7e1f19ab-0ae5-47d3-93b8-5b904a8c825b" />

https://github.com/user-attachments/assets/858f8425-8fb2-4c8f-9999-755523f6ae67

## 技术栈

- Java 17
- JSoup ：HTML解析
- OkHttp ：网络请求
- Jackson ：JSON处理
- Hutool ：工具库
- Lombok ：代码简化
- SLF4J & Logback ：日志处理
- Spring Boot 3.2.3 ：应用框架
- Spring AI ：AI集成

## 快速开始

### 环境要求

- JDK 17 或更高版本

### 安装步骤

1. 克隆项目到本地：

```bash
git clone https://github.com/yourusername/web-monitor.git
cd web-monitor
```

2. 使用Maven编译项目：

```bash
mvn clean package
```

### 基本配置

1. 在 `src/main/resources/application.yml` 中配置监控目标：

```yaml
web-monitor:
  configs:
     - type: AIFetcher
       name: AIMonitor
       url: https://www.baidu.com
       intervalSeconds: 600
       enabled: true
       userQuery: 获取第一个热搜的标题
  observers:
    - type: email
      host: smtp.example.com
      port: 587
      username: your-email@example.com
      password: your-password
      enabled: true
```

### 使用示例

```java
// 通过Spring Boot启动应用
@SpringBootApplication
public class WebMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebMonitorApplication.class, args);
    }
}

// 应用启动后将自动加载配置并开始监控，同时打开图形化日志界面
```

## 配置说明

### 监控目标配置

- `url`: 要监控的网页URL
- `interval`: 监控间隔（秒）
- `selector`: CSS选择器，用于定位要监控的内容
- `category`: 内容分类

### 观察者配置

支持多种通知方式：

- 邮件通知
  ````
   注意：QQ邮箱的password不是QQ密码，而是授权码
   获取授权码步骤：
   1. 登录QQ邮箱网页版
   2. 点击"设置" -> "账户"
   3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
   4. 开启"POP3/SMTP服务"
   5. 点击"生成授权码"
  ````
- Slack通知
  ````
   Slack Webhook配置说明：
   1. 访问 https://api.slack.com/apps
   2. 点击"Create New App"
   3. 选择"From scratch"
   4. 输入应用名称和工作区
   5. 在"Features"中选择"Incoming Webhooks"
   6. 激活Incoming Webhooks
   7. 点击"Add New Webhook to Workspace"
   8. 选择要发送消息的频道
   9. 复制Webhook URL
  ````
- 企业微信通知
- 自定义通知

## 高级功能

### 自定义内容解析

可以通过实现 `ContentFetcher` 接口来自定义内容解析逻辑：

```java
public class CustomFetcher implements ContentFetcher {
    @Override
    public List<WebContent> fetch() {
        // 自定义解析逻辑
    }
}
```

### 自定义通知

可以通过实现 `WebObserver` 接口来自定义通知方式：

```java
public class CustomObserver implements WebObserver {
    @Override
    public void send(List<WebContent> contents) {
        // 自定义通知逻辑
    }
}
```

## 常见问题

1. **xxxx？**
    - xxxx

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。
