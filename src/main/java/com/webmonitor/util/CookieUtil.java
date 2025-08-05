package com.webmonitor.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CookieUtil {

  static String COOKIE_PATH = "./cookie.json";
  static ChromeDriver CHROME_DRIVER = null;

  public static void main(String[] args) {
    CHROME_DRIVER = SeleniumUtil.getChromeDriver();
    CHROME_DRIVER.get("https://www.zhihu.com/hot");
    if (isCookieValid(COOKIE_PATH)) {
      loadCookie(COOKIE_PATH);
      CHROME_DRIVER.navigate().refresh();
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Sleep was interrupted", e);
      }
    }
    log.error("cookie失效，尝试登录。。。。");
    scanLogin();
  }


  private static void scanLogin() {
    log.info("等待登陆..");
    WebDriverWait wait = new WebDriverWait(CHROME_DRIVER, Duration.ofSeconds(40));

    try {
      TimeUnit.SECONDS.sleep(60);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Sleep was interrupted", e);
    }
//    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("hasresume")));
    saveCookie(COOKIE_PATH, CHROME_DRIVER);
  }

  public static void saveCookie(String path, ChromeDriver CHROME_DRIVER) {
    // 获取所有的cookies
    Set<Cookie> cookies = CHROME_DRIVER.manage().getCookies();
    // 保存所有的cookie信息
    JSONArray jsonArray = new JSONArray();
    for (Cookie cookie : cookies) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("name", cookie.getName());
      jsonObject.put("value", cookie.getValue());
      jsonObject.put("domain", cookie.getDomain());
      jsonObject.put("path", cookie.getPath());
      if (cookie.getExpiry() != null) {
        jsonObject.put("expiry", cookie.getExpiry().getTime());
      }
      jsonObject.put("isSecure", cookie.isSecure());
      jsonObject.put("isHttpOnly", cookie.isHttpOnly());
      jsonArray.put(jsonObject);
    }
    saveCookieToFile(jsonArray, path);
  }

  private static void saveCookieToFile(JSONArray jsonArray, String path) {
    try (FileWriter file = new FileWriter(path)) {
      log.info(String.valueOf(jsonArray));
      file.write(jsonArray.toJSONString(4));  // 使用4个空格的缩进
      log.info("Cookie已保存到文件：{}", path);
    } catch (IOException e) {
      log.error("保存cookie异常。保存路径:{}", path);
    }
  }

  public static boolean isCookieValid(String cookiePath) {
    return Files.exists(Paths.get(cookiePath));
  }

  public static void loadCookie(String cookiePath) {
    cookiePath = COOKIE_PATH;
    // 清除由于浏览器打开已有的cookies
    CHROME_DRIVER.manage().deleteAllCookies();
    // 从文件中读取JSONArray
    JSONArray jsonArray = null;
    try {
      String jsonText = new String(Files.readAllBytes(Paths.get(cookiePath)));
      if (!jsonText.isEmpty()) {
        jsonArray = new JSONArray(jsonText);
      }
    } catch (IOException e) {
      log.error("读取cookie异常");
    }
    // 遍历JSONArray中的每个JSONObject，并从中获取cookie的信息
    if (jsonArray != null) {
      for (int i = 0; i < jsonArray.size(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        String name = jsonObject.getStr("name");
        String value = jsonObject.getStr("value");
        String domain = jsonObject.getStr("domain");
        String path = jsonObject.getStr("path");
        Date expiry = null;
        if (!jsonObject.isNull("expiry")) {
          expiry = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
          jsonObject.put("expiry", Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()); // 更新expiry
        }
        boolean isSecure = jsonObject.getBool("isSecure");
        boolean isHttpOnly = jsonObject.getBool("isHttpOnly");
        // 使用这些信息来创建新的Cookie对象，并将它们添加到WebDriver中
        Cookie cookie = new Cookie.Builder(name, value)
                .domain(domain)
                .path(path)
                .expiresOn(expiry)
                .isSecure(isSecure)
                .isHttpOnly(isHttpOnly)
                .build();
        try {
          CHROME_DRIVER.manage().addCookie(cookie);
        } catch (Exception ignore) {
        }
      }
      // 更新cookie文件
      updateCookieFile(jsonArray, cookiePath);
    }
  }

  private static void updateCookieFile(JSONArray jsonArray, String path) {
    // 将JSONArray写入到一个文件中
    try (FileWriter file = new FileWriter(path)) {
      file.write(jsonArray.toJSONString(4));  // 使用4个空格的缩进
      log.info("cookie文件更新：{}", path);
    } catch (IOException e) {
      log.error("更新cookie异常。保存路径:{}", path);
    }
  }

}
