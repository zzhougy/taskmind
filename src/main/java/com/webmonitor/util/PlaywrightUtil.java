package com.webmonitor.util;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class PlaywrightUtil {

  public static String getHtml(String url) {
    Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
    Faker faker = new Faker();
    String userAgent = faker.internet().userAgent(Internet.UserAgent.CHROME);
    contextOptions.setUserAgent(userAgent);
    contextOptions.setViewportSize(1200, 800);// 设置窗口宽高

    // 有头模式-false查看调试
    // 无头模式-true节省资源
    BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(false);

    try (Playwright playwright = Playwright.create();
         Browser browser = playwright.chromium().launch(launchOptions);
         BrowserContext context = browser.newContext(contextOptions);
    ) {
      System.out.println("百度开始 " + url);
      Page page = context.newPage();
      page.navigate(url, new Page.NavigateOptions().setTimeout(10000));
      page.waitForLoadState(LoadState.NETWORKIDLE);

      System.out.println("标题 " + page.title());
      System.out.println("当前链接 " + page.url());



//      // 获取搜索框
//      Locator searchInput = page.locator("#kw");
//      System.out.println("搜索框是否存在: " + (searchInput.count() > 0));
//
//      // 获取搜索按钮
//      Locator searchButton = page.locator("#su");
//      System.out.println("搜索按钮是否存在: " + (searchButton.count() > 0));
//
//      // 在搜索框中输入内容
//      if (searchInput.count() > 0) {
//        searchInput.fill("WebMonitor");
//        System.out.println("已在搜索框中输入 'WebMonitor'");
//      }

//      // 获取页面链接数量
//      Locator links = page.locator("a");
//      System.out.println("页面链接数量: " + links.count());
//
//      // 获取页面图片数量
//      Locator images = page.locator("img");
//      System.out.println("页面图片数量: " + images.count());

      System.out.println("页面操作完成");

      // 等待一段时间以便观察
      page.waitForTimeout(5000);


      return page.content();
    }

  }

  /**
   * 获取重定向跳转地址
   *
   * @param url
   * @return
   */
  public static String getLocation(String url) {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    HttpHeaders responseHeaders = response.getHeaders();
    return Objects.requireNonNull(responseHeaders.getLocation()).toString();
  }

}