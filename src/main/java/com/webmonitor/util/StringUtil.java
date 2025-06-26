package com.webmonitor.util;

import io.micrometer.common.util.StringUtils;

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

}
