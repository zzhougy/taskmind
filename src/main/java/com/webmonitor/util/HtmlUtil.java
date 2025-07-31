package com.webmonitor.util;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.webmonitor.config.exception.SystemException;
import com.webmonitor.constant.WayToGetHtmlEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

@Slf4j
public class HtmlUtil {

  public static final int MAX_HTML_SIZE = 300000;
  public static final long INTERVAL_MS = 60000;

  public static Document getDocumentByJsoup(String url, Map<String, String> headers, String cookie) throws IOException {
    log.info("===== [getHtml] Waiting.....");
    IntervalLimiter.awaitNext(StringUtil.getHost(url), INTERVAL_MS);
    log.info("===== [getHtml] Start");
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
    log.info("===== [getDocument] End Success, {}", url);
    return connect.get();
  }

  public static String getHtmlByJsoup(String url, Map<String, String> headers, String cookie) throws IOException {
    String html = getDocumentByJsoup(url, headers, cookie).html();
    log.info("===== [getHtml] End Success, url: {}, htmlSize: {}", url, html.length());
    return html;
  }

  public static String getHtmlBySelenium(String url) throws MalformedURLException {
    log.info("===== [getHtmlBySelenium] Waiting.....");
    IntervalLimiter.awaitNext(StringUtil.getHost(url), INTERVAL_MS);
    log.info("===== [getHtmlBySelenium] Start");
    ChromeDriver driver = SeleniumUtil.getChromeDriver();
    // 打开目标网页
    driver.get(url);
    String html = driver.getPageSource();
    log.info("===== [getHtmlBySelenium] End Success, url: {}, htmlSize: {}", url, html.length());
    return html;
  }

  public static String getHtmlByPlaywright(String url) throws MalformedURLException {
    log.info("===== [getHtmlByPlaywright] Waiting.....");
    IntervalLimiter.awaitNext(StringUtil.getHost(url), INTERVAL_MS);
    log.info("===== [getHtmlByPlaywright] Start");
    String htmlContent = PlaywrightUtil.getHtml(url);
    log.info("===== [getHtmlByPlaywright] End Success, url: {}, htmlSize: {}", url, htmlContent.length());
    return htmlContent;
  }

  public static Document getDocumentBySelenium(String url) throws MalformedURLException {
    String html = getHtmlBySelenium(url);
    Document parse = Jsoup.parse(html);
    return parse;
  }

  public static Document getDocumentByPlaywright(String url) throws MalformedURLException {
    String html = getHtmlByPlaywright(url);
    Document parse = Jsoup.parse(html);
    return parse;
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

  public static String cleanHtml(String html) throws Exception {
    String cleanedHtml = HtmlUtil.extractBodyByJsoup(html);
    log.info("[getSelectorFromAI] 原始htmlSize:{}, 截取主要html后的htmlSize:{}", html.length(), cleanedHtml.length());
    if (cleanedHtml.length() >= MAX_HTML_SIZE) {
      throw new Exception("网页内容过长，暂不处理");
    }

    cleanedHtml = SensitiveUtil.filterSensitiveWords(cleanedHtml);

    return cleanedHtml;
  }

  public static Document getDocumentByWayToGetHtml(String url, WayToGetHtmlEnum wayToGetHtml) throws IOException {
    if (wayToGetHtml == WayToGetHtmlEnum.JSOUP) {
      return HtmlUtil.getDocumentByJsoup(url, null, null);
    } else if (wayToGetHtml == WayToGetHtmlEnum.SELENIUM) {
      return HtmlUtil.getDocumentBySelenium(url);
    } else if (wayToGetHtml == WayToGetHtmlEnum.PLAYWRIGHT) {
      return HtmlUtil.getDocumentByPlaywright(url);
    }
    log.info("===== [getDocumentByWayToGetHtml] End Success, url: {}, wayToGetHtml: {}", url, wayToGetHtml);
    throw new SystemException("Invalid wayToGetHtml: " + wayToGetHtml);
  }

}
