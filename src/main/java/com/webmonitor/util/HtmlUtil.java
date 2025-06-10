package com.webmonitor.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class HtmlUtil {


  public static String getHtml(String url, Map<String, String> headers) throws IOException {
    Document document;
    if (headers != null && !headers.isEmpty()) {
      document = Jsoup.connect(url)
              .headers(headers)
              .timeout(10000)
              .get();
    } else {
      document = Jsoup.connect(url)
              .timeout(10000)
              .get();
    }
    return document.html();
  }


}
