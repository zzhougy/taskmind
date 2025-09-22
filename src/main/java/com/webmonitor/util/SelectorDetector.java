package com.webmonitor.util;

public class SelectorDetector {
  public static void main(String[] args) {
    // 测试案例
    String cssTest1 = "哈哈https://weibo.com/meituan哈哈哈div.classname > ul#idname li:first-child";
    String cssTest2 = "这是一个普通的字符串，没有特殊内容";
    String cssTest3 = "哈哈https://weibo.com/meituan哈哈哈";
    String cssTest4 = "哈哈weibo.com/meituan哈哈哈";
    String cssTest5 = "1天后打开www.baidu.com，告诉我li.hotsearch-item:nth-child(3) > a:nth-child(1) > span:nth-child(4)是什么";
    String cssTest6 = "哈哈哈哈哈https://weibo.com/meituan ，div.classname > ul#idname li:first-child";
    System.out.println(maybeCssSelector(cssTest1));
    System.out.println(maybeCssSelector(cssTest2));
    System.out.println(maybeCssSelector(cssTest3));
    System.out.println(maybeCssSelector(cssTest4));
    System.out.println(maybeCssSelector(cssTest5));
    System.out.println(maybeCssSelector(cssTest6));

    System.out.println("========================");

    String xpathTest1 = "哈哈https://weibo.com/meituan哈哈哈//div[@class='classname']/ul[@id='idname']/li[1]";
    String xpathTest2 = "这是一个普通的字符串，没有特殊内容";
    String xpathTest3 = "哈哈https://weibo.com/meituan哈哈哈";
    String xpathTest4 = "哈哈weibo.com/meituan哈哈哈";
    String xpathTest5 = "1天后打开www.baidu.com，告诉我//div[@class='classname']/ul[@id='idname']/li[1]是什么";
    String xpathTest6 = "哈哈哈哈哈https://weibo.com/meituan ，//div[@class='classname']/ul[@id='idname']/li[1]";
    System.out.println(maybeXPath(xpathTest1));
    System.out.println(maybeXPath(xpathTest2));
    System.out.println(maybeXPath(xpathTest3));
    System.out.println(maybeXPath(xpathTest4));
    System.out.println(maybeXPath(xpathTest5));
    System.out.println(maybeXPath(xpathTest6));


  }

  public static boolean maybeCssSelector(String s) {
    if (s == null) {
      return false;
    }
    // 检查组合器：>、+、~（前后可能有空格）
    if (s.matches(".*\\s*[>+~]\\s*.*")) {
      return true;
    }
    // 检查伪类：:后跟字母，且不包含 ://（避免误判URL协议）
    if (s.matches(".*:[a-zA-Z].*") && !s.contains("://")) {
      return true;
    }
    // 检查属性选择器：[ followed by any characters until ]
    if (s.matches(".*\\[.+\\].*")) {
      return true;
    }
    return false;
  }


  /**
   * 检测字符串中是否包含XPath表达式
   */
  public static boolean maybeXPath(String input) {
    // 是否包含"//"和"[", 或者以"/"开头并包含"["
    return input.contains("//") && input.contains("[");
  }
}