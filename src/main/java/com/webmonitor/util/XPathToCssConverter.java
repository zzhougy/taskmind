package com.webmonitor.util;

import java.util.HashMap;
import java.util.Map;

public class XPathToCssConverter {

  private static final Map<String, String> xpathToCssMap = new HashMap<>();

  static {
    xpathToCssMap.put("//", ""); // 根节点
    xpathToCssMap.put("/", " > "); // 子节点
    xpathToCssMap.put("/*", "*"); // 所有子节点
    xpathToCssMap.put("@", ""); // 属性
    xpathToCssMap.put("text()", ""); // 文本
    xpathToCssMap.put("node()", ""); // 任何节点
  }

  public static String convert(String xpath) {
    // 处理基本的转换
    String css = xpath;

    // 替换根节点
    css = css.replaceAll("^//", "");
    css = css.replaceAll("/", " > ");
    css = css.replaceAll("\\*", "*"); // 所有元素
    css = css.replaceAll("@", ""); // 属性处理
    css = css.replaceAll("text\\(\\)", ""); // 文本处理
    css = css.replaceAll("node\\(\\)", ""); // 任何节点处理

    // 处理其他 XPath 特性
    css = handleAttributes(css);
    css = handleConditions(css);

    return css.trim();
  }

  private static String handleAttributes(String css) {
    // 处理属性选择器
    return css.replaceAll("\\[@(\\w+)\\]", "[$1]");
  }

  private static String handleConditions(String css) {
    // 处理条件（如 [position()=1] 转换为 :first-child）
    css = css.replaceAll("\\[position\\(\\)=(\\d+)\\]", ":nth-child($1)");
    return css;
  }

  public static void main(String[] args) {
    String xpath = "//div[@class='example']/span/text()";

    xpath = "//*[@id=\"lg\"]/map/area/@title";
    String css = convert(xpath);
    System.out.println("XPath: " + xpath);
    System.out.println("CSS: " + css);
  }
}
