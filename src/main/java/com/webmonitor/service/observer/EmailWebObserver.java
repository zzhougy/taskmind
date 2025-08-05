package com.webmonitor.service.observer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import com.webmonitor.config.observer.EmailObserverConfig;
import com.webmonitor.core.WebContent;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@Slf4j
public class EmailWebObserver implements WebObserver {
  private static final String EMAIL_HOST_PROPERTY = "email.host";
  private static final String EMAIL_PORT_PROPERTY = "email.port";
  private static final String EMAIL_USERNAME_PROPERTY = "email.username";
  private static final String EMAIL_PASSWORD_PROPERTY = "email.password";
  private static final String EMAIL_TO_PROPERTY = "email.to";
  private static final String EMAIL_FROM_PROPERTY = "email.from";
  public static final String SENDER_NAME = "这是发送人的测试";

  private final Session session;
  private final String toEmail;
  private final String fromEmail;

  private final EmailObserverConfig emailObserverConfig;

  public EmailWebObserver(EmailObserverConfig emailObserverConfig) {
    this.emailObserverConfig = emailObserverConfig;
    Properties props = loadEmailProperties();
    this.session = createMailSession(props);
    this.toEmail = props.getProperty(EMAIL_TO_PROPERTY);
    this.fromEmail = props.getProperty(EMAIL_FROM_PROPERTY);
  }

  private Properties loadEmailProperties() {
    try {
      Properties mailProps = new Properties();
      // QQ邮箱SMTP服务器配置
      mailProps.put("mail.smtp.host", this.emailObserverConfig.getHost());
      mailProps.put("mail.smtp.port", this.emailObserverConfig.getPort());
      mailProps.put("mail.smtp.auth", "true");
      mailProps.put("mail.smtp.ssl.enable", "true"); // 启用SSL
      mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 指定SSL协议版本
      mailProps.put(EMAIL_USERNAME_PROPERTY, this.emailObserverConfig.getUsername());
      mailProps.put(EMAIL_PASSWORD_PROPERTY, this.emailObserverConfig.getPassword());
      mailProps.put(EMAIL_TO_PROPERTY, this.emailObserverConfig.getTo());
      mailProps.put(EMAIL_FROM_PROPERTY, this.emailObserverConfig.getFrom());

      return mailProps;
    } catch (Exception e) {
      throw new RuntimeException("加载邮件配置失败", e);
    }
  }

  private Session createMailSession(Properties props) {
    return Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(
          props.getProperty(EMAIL_USERNAME_PROPERTY),
          props.getProperty(EMAIL_PASSWORD_PROPERTY)
        );
      }
    });
  }

  @Override
  public void send(List<WebContent> webContents) {
    if (CollectionUtil.isEmpty(webContents)) {
      log.warn("收到空的URL或内容更新");
      return;
    }

    try {
      String messageContent = formatMessage(webContents);
      sendEmail("网站内容更新通知", messageContent);
    } catch (Exception e) {
      log.error("发送邮件通知失败", e);
    }
  }

  public static void main(String[] args) {
    // 测试formatMessage方法
    List<WebContent> webContents = List.of(
//            Builder
            WebContent.builder()
                    .title("测试标题")
                    .description("测试描述")
                    .url("https://www.baidu.com")
                    .source("百度")
                    .dateStr("2023-05-05")
                    .category("测试分类")
                    .build(),

            WebContent.builder()
                    .title("测试标题2")
                    .description("测试描述2")
                    .url("https://www.baidu.com")
                    .source("百度")
                    .dateStr("2023-05-05")
                    .category("测试分类")
                    .build()
    );
    // formatMessage
    String s = formatMessage(webContents);
    log.info("格式化后的消息：{}", s);
  }

  private static String formatMessage(List<WebContent> webContents) {
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("🔔 网站内容通知\n\n");
    messageBuilder.append("📅 时间：").append(LocalDateTime.now().format(AbstractMessageObserver.DATE_FORMATTER)).append("\n\n");
    messageBuilder.append("📝 内容：\n");

    for (WebContent content : webContents) {
      messageBuilder.append("\n来源：").append(content.getSource());
      messageBuilder.append("\n类别：").append(content.getCategory());
      if (content.getDateStr() != null && !content.getDateStr().isEmpty()) {
        messageBuilder.append("\n时间：").append(content.getDateStr());
      }
      messageBuilder.append("\n标题：").append(content.getTitle());
      if (content.getDescription() != null && !content.getDescription().isEmpty()) {
        messageBuilder.append("\n描述：").append(content.getDescription());
      }
      messageBuilder.append("\n链接：").append(content.getUrl());
      messageBuilder.append("\n------------------------\n");
    }

    return messageBuilder.toString();
  }

  private void sendEmail(String subject, String content) throws MessagingException, UnsupportedEncodingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(fromEmail, SENDER_NAME, CharsetUtil.UTF_8));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
    message.setSubject(subject);
    message.setText(content);

    Transport.send(message);
    log.info("邮件通知发送成功");
  }
} 