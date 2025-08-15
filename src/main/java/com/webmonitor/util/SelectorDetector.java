package com.webmonitor.util;

public class SelectorDetector {
  public static void main(String[] args) {
    String input = "1天后打开www.baidu.com，告诉我li.hotsearch-item:nth-child(3) > a:nth-child(1) > span:nth-child(4)是什么";

    System.out.println("可能包含CSS Selector: " + maybeCssSelector(input));
    System.out.println("可能包含XPath: " + maybeXPath(input));
  }

  public static boolean maybeCssSelector(String s) {
    if (s == null || s.trim().isEmpty()) return false;
    return s.matches(".*[\\s>+~]*[.#]?[\\w-]+([\\[:(]|\\s|$).*") ||
            s.matches(".*\\w+[.#:]\\w+.*");
  }

  public static boolean maybeXPath(String s) {
    if (s == null || s.trim().isEmpty()) return false;
    return s.matches("^(//|/)[\\w\\-@\\*\\[\\]]+.*") ||
            s.matches(".*@[\\w-]+.*") ||
            s.matches(".*\\[.*\\].*");
  }
}