package com.webmonitor.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class HtmlUtil {

  public static Document getDocument(String url, Map<String, String> headers, String userAgent) throws IOException {
    Connection connect = Jsoup.connect(url).timeout(10000);
    if (headers != null) {
      connect.headers(headers);
    }
    if (userAgent != null) {
      connect.userAgent(userAgent);
    }
    return connect.get();
  }

  public static String getHtml(String url, Map<String, String> headers, String userAgent) throws IOException {
    return getDocument(url, headers, userAgent).html();
  }


}
