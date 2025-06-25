package com.webmonitor.fetcher;

import com.webmonitor.config.fetcher.SeleniumFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SeleniumFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final SeleniumFetcherConfig seleniumFetcherConfig;
  private WebDriver driver;
  private WebDriverWait wait;

  public SeleniumFetcher(SeleniumFetcherConfig seleniumFetcherConfig) {
    this.seleniumFetcherConfig = seleniumFetcherConfig;
    System.setProperty("webdriver.chrome.driver", seleniumFetcherConfig.getDriverPath());
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", seleniumFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", seleniumFetcherConfig.getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();

    try {
      // 初始化WebDriver
      ChromeOptions options = new ChromeOptions();
//      options.addArguments("--headless=new");
//      options.addArguments("--disable-gpu");
//      options.addArguments("--no-sandbox");
      driver = new ChromeDriver(options);
      wait = new WebDriverWait(driver, Duration.ofSeconds(seleniumFetcherConfig.getTimeout()));

      // 打开目标网页
      driver.get(seleniumFetcherConfig.getUrl());


      if (!StringUtils.isEmpty(seleniumFetcherConfig.getCssSelector())
      && !StringUtils.isEmpty(seleniumFetcherConfig.getXPath())) {
        throw new RuntimeException("CSS选择器和XPath不能同时使用");
      }

      String selector = !StringUtils.isEmpty(seleniumFetcherConfig.getCssSelector())
              ?  seleniumFetcherConfig.getCssSelector() : seleniumFetcherConfig.getXPath();
      String[] parts = selector.split("\\|");
      String cssSelector = parts[0];
      String attributePart = parts[1];
      if (parts.length != 2) {
        throw new RuntimeException("无效的选择器格式: " + selector);
      }
      WebElement element = null;
      if (!StringUtils.isEmpty(seleniumFetcherConfig.getCssSelector())) {
        element = wait.until(d -> d.findElement(By.cssSelector(cssSelector)));
      } else {
        element = wait.until(d -> d.findElement(By.xpath(seleniumFetcherConfig.getXPath())));
      }
      String title = null;
      if ("text".equals(attributePart)) {
        title = element.getText(); // 获取文本内容
      } else if (attributePart.startsWith("attr(") && attributePart.endsWith(")")) {
        String attribute = attributePart.substring(5, attributePart.length() - 1);
        title = element.getAttribute(attribute); // 获取指定属性值
      } else {
        throw new RuntimeException("无效的属性部分: " + attributePart);
      }

//      WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("kw")));
//      String pageSource = driver.getPageSource();
//      System.out.println("页面源码是：" + pageSource);
//      String s = CssSelectorUtil.cssParse(pageSource, "*[id=\"lg\"] > map > area[shape='rect']|attr(title)");
//      System.out.println("s is " + s);
//      WebElement settingsLink = driver.findElement(By.cssSelector("*[id=\"lg\"] > map > area[shape='rect']"));
//      WebElement settingsLink = driver.findElement(By.xpath("*[id=\"lg\"] > map > area[shape='rect']"));

      WebContent webContent = WebContent.builder()
              .id(title)
              .title(title)
              .description(title)
              .link(seleniumFetcherConfig.getUrl())
              .source(seleniumFetcherConfig.getName())
              .dateStr(null)
              .category(seleniumFetcherConfig.getName())
              .build();

      currentWeb.add(webContent);

    } catch (Exception e) {
      log.error("Selenium获取内容失败", e);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", seleniumFetcherConfig.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条内容，不通知", seleniumFetcherConfig.getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }
}