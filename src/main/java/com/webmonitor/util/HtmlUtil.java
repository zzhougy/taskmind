package com.webmonitor.util;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class HtmlUtil {

  public static Document getDocument(String url, Map<String, String> headers) throws IOException {
    Connection connect = Jsoup.connect(url).timeout(10000);
    if (headers != null) {
      connect.headers(headers);
    }
    Faker faker = new Faker();
    String userAgent = faker.internet().userAgent(Internet.UserAgent.CHROME);
    connect.userAgent(userAgent);
    return connect.get();
  }

  public static String getHtml(String url, Map<String, String> headers) throws IOException {
    return getDocument(url, headers).html();
  }


}
