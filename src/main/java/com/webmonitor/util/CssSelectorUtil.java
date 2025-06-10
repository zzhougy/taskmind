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
    String html = HtmlUtil.getHtml(url, headers);
    Map<String, String> result = new LinkedHashMap<>();

    for (Map.Entry<String, String> entry : selectorDict.entrySet()) {
      String key = entry.getKey();
      String cssSelector = entry.getValue();

      String value = cssParse(html, cssSelector);
      result.put(key, value);
    }

    return result;
  }

  static String cssParse(String html, String cssSelector) {
    try {
      Document document = Jsoup.parse(html);
      Elements elements = document.select(cssSelector);

      if (!elements.isEmpty()) {
        Element first = elements.first();
        return first != null ? first.attr("title") : null;
      }

      throw new RuntimeException("无法获取文本信息");
    } catch (Exception e) {
      throw new RuntimeException("css 解析失败: " + e.getMessage());
    }
  }


}
