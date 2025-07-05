package com.webmonitor.util;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class HtmlUtil {

  public static Document getDocument(String url, Map<String, String> headers, String cookie) throws IOException {
    log.info("[getHtml] Start");
    Connection connect = Jsoup.connect(url).timeout(10000);
    if (headers != null) {
      connect.headers(headers);
    }
    Faker faker = new Faker();
    String userAgent = faker.internet().userAgent(Internet.UserAgent.CHROME);
    connect.userAgent(userAgent);
    if (!StringUtils.isEmpty(cookie)) {
      connect.cookie("Cookie", cookie);
    }
    log.info("[getDocument] End Success, {}", url);
    return connect.get();
  }

  public static String getHtml(String url, Map<String, String> headers, String cookie) throws IOException {
    log.info("[getHtml] Start");
    String html = getDocument(url, headers, cookie).html();
    log.info("[getHtml] End Success, url: {}, htmlSize: {}", url, html.length());
    return html;
  }

  public static String getHtmlBySelenium(String url) {
    log.info("[getHtmlBySelenium] Start");
    ChromeDriver driver = SeleniumUtil.getChromeDriver();
    // 打开目标网页
    driver.get(url);
    String html = driver.getPageSource();
    log.info("[getHtmlBySelenium] End Success, url: {}, htmlSize: {}", url, html.length());
    return html;
  }


  /**
   * 使用Jsoup库提取body内容并过滤多余标签
   */
  public static String extractBodyByJsoup(String html) {
    if (html == null) {
      return "";
    }
    try {
      Document doc = Jsoup.parse(html);
      Element body = doc.body();
      if (body != null) {
        body.select("script").remove();
        body.select("style").remove();
        body.select("textarea").remove();
        return body.html();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }


}
