package com.webmonitor.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CssSelectorUtil {


  public static Map<String, String> getByCssSelector(String url, Map<String, String> selectorDict, Map<String, String> headers) throws IOException {
    String html = HtmlUtil.getHtml(url, headers, null);
    System.out.println( html);
    Map<String, String> result = new LinkedHashMap<>();

    for (Map.Entry<String, String> entry : selectorDict.entrySet()) {
      String key = entry.getKey();
      String cssSelector = entry.getValue();

      String value = cssParse(html, cssSelector);
      result.put(key, value);
    }

    return result;
  }

  public static String cssParse(String html, String cssSelectorFull) {
    try {
      Document document = Jsoup.parse(html);

      // 分割自定义选择器
      String[] parts = StringUtil.splitAndCheckSelectorStr(cssSelectorFull);
      String cssSelector = parts[0];
      String attributePart = parts[1];

      Elements elements = document.select(cssSelector);

      if (!elements.isEmpty()) {
        Element first = elements.first();
        if ("text".equals(attributePart)) {
          return first.text(); // 获取文本内容
        } else {
          return first.attr(StringUtil.getAttribute(attributePart)); // 获取指定属性值
        }
      } else {
        throw new RuntimeException("未找到指定元素");
      }

    } catch (Exception e) {
      throw new RuntimeException("css 解析失败: " + e.getMessage());
    }
  }


}
