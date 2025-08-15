package com.webmonitor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlChecker {
  // 简单的 URL 正则表达式（支持 http、https、ftp、www 开头）
  private static final String URL_REGEX = "((https?|ftp)://|www\\.)[\\w\\-]+(\\.[\\w\\-]+)+([/?#][^\\s]*)?";
  private static final Pattern pattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

  public static boolean containsUrl(String text) {
    if (text == null || text.isEmpty()) {
      return false;
    }
    Matcher matcher = pattern.matcher(text);
    return matcher.find();
  }

  public static void main(String[] args) {
    String test1 = "Check this link: https://example.com";
    String test2 = "No links here!";
    String test3 = "Visit www.example.top for more info.";

    System.out.println(containsUrl(test1)); // true
    System.out.println(containsUrl(test2)); // false
    System.out.println(containsUrl(test3)); // true
  }
}