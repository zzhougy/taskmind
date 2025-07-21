package com.webmonitor.util;

import io.micrometer.common.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class StringUtil {

  public static String[] splitAndCheckSelectorStr(String selectorStr) {
    if (StringUtils.isEmpty(selectorStr) || !selectorStr.contains("|")) {
      throw new RuntimeException("无效的选择器格式: " + selectorStr);
    }
    String[] parts = selectorStr.split("\\|");
    String cssSelector = parts[0];
    String attributePart = parts[1];
    if (parts.length != 2 || StringUtils.isEmpty(cssSelector)) {
      throw new RuntimeException("无效的选择器格式: " + selectorStr);
    }
    if (!"text".equals(attributePart) && !(attributePart.startsWith("attr(") && attributePart.endsWith(")"))) {
      throw new RuntimeException("无效的属性部分: " + attributePart);
    }
    return parts;
  }


  public static String getAttribute(String attrStr) {
    return attrStr.substring(5, attrStr.length() - 1);
  }


  public static String getHost(String url) throws MalformedURLException {
    URL urlObj = new URL(url);
    return urlObj.getHost();
  }

  public static void main(String[] args) throws Exception {
    String urlString = "https://sub.example.com:8080/path/page?query=123#fragment";
    URL url = new URL(urlString);
    String host = url.getHost(); // 返回域名，不含端口
    System.out.println("域名: " + host); // 输出: sub.example.com
  }
}
