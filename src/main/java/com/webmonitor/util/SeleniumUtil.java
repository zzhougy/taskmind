package com.webmonitor.util;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.Internet;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public class SeleniumUtil {
  public static ChromeDriver getChromeDriver() {
    // 初始化WebDriver
    ChromeOptions options = new ChromeOptions();
    // Chrome 112+ 引入的“新版”无头模式
      options.addArguments("--headless=new");
    // 旧版无头模式（Chrome 109 及以前）
//      options.addArguments("--headless");
    // 禁用GPU加速，解决无头模式渲染问题
    options.addArguments("--disable-gpu");
    // ============ Start 连续获取同一页面两次，第一次正常，第二次拿到的是很久之前的数据
    options.addArguments("--disable-application-cache"); // 禁用应用缓存
    options.addArguments("--disable-cache");             // 禁用内存/磁盘缓存
    options.addArguments("--disk-cache-size=0");         // 磁盘缓存大小设为 0

    // 在 URL 加时间戳或随机数，时间戳每次不同，浏览器/代理都会认为是“新”请求，强制回源。
//    String url = "https://example.com/page?_t=" + System.currentTimeMillis();

//    用 DevTools 指令清空缓存
//    driver.getDevTools().createSession();
//    driver.getDevTools().send(Network.clearBrowserCookies()); // 清 Cookie
//    driver.getDevTools().send(Network.clearBrowserCache());   // 清缓存
//（需 Selenium 4 + Chrome DevTools 支持）
    // =============== End


    String userAgent = new Faker().internet().userAgent(Internet.UserAgent.CHROME);
    options.addArguments("--user-agent=" + userAgent);
    // 禁用沙箱。适用场景：Linux服务器环境
    options.addArguments("--no-sandbox");
    options.addArguments("--window-size=1920,1080");
    // 禁用密码保存提示
    options.addArguments("--disable-save-password-bubble");
    // 设置浏览器不被检测
    options.addArguments("--disable-blink-features=AutomationControlled");
    // 排除自动化标志
    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
    ChromeDriver driver = new ChromeDriver(options);
    // 在页面加载前覆盖关键属性，使 navigator.webdriver 返回 undefined
    String js = "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });";
    ((JavascriptExecutor) driver).executeScript(js);
    return driver;
  }

  public static WebDriverWait getWebDriverWait(ChromeDriver driver, int timeoutSeconds) {
    return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
  }


  public static WebDriverWait getWebDriverWait(int timeoutSeconds) {
    return new WebDriverWait(getChromeDriver(), Duration.ofSeconds(timeoutSeconds));
  }

}
