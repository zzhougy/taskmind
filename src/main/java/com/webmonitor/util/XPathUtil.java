package com.webmonitor.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class XPathUtil {


  public static Map<String, String> getXpathSelector(String url, Map<String, String> selectorDict, Map<String, String> headers) throws IOException {
    String html = HtmlUtil.getHtml(url, headers);
    Map<String, String> result = new LinkedHashMap<>();

    for (Map.Entry<String, String> entry : selectorDict.entrySet()) {
      String key = entry.getKey();
      String xpathSelector = entry.getValue();

      String convert = XPathToCssConverter.convert(xpathSelector);

      String value = CssSelectorUtil.cssParse(html, convert);
      result.put(key, value);
    }

    return result;
  }

}
