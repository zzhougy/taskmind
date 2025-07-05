package com.webmonitor.util;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumUtil {
  public static ChromeDriver getChromeDriver() {
    // 初始化WebDriver
    ChromeOptions options = new ChromeOptions();
//      options.addArguments("--headless=new");
//      options.addArguments("--headless");
    // 禁用GPU加速，解决无头模式渲染问题
    options.addArguments("--disable-gpu");
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
