package com.webmonitor.fetcher;

import com.webmonitor.config.fetcher.XPathFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XPathFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final XPathFetcherConfig xPathFetcherConfig;

  public XPathFetcher(XPathFetcherConfig xPathFetcherConfig) {
    this.xPathFetcherConfig = xPathFetcherConfig;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", xPathFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", xPathFetcherConfig.getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();

    Document document = Jsoup.connect(xPathFetcherConfig.getUrl()).get();

    JXDocument jxDocument = JXDocument.create(document);

    String[] parts = StringUtil.splitAndCheckSelectorStr(xPathFetcherConfig.getXPath());
    String selector = parts[0];
    String attributePart = parts[1];


    String title = null;
    List<JXNode> jxNodes = jxDocument.selN(selector);
    if ("text".equals(attributePart)) {
      for (JXNode jxNode : jxNodes) {
        Element element = jxNode.asElement();
        System.out.println(element.text());
        title = element.text();
      }
    } else {
      for (JXNode jxNode : jxNodes) {
        Element element = jxNode.asElement();
        System.out.println(element.text());
        title = element.attr(StringUtil.getAttribute(attributePart));
      }
    }


    WebContent webContent = WebContent.builder()
            .id(title)
            .title(title)
            .description(title)
            .link(null)
            .source(xPathFetcherConfig.getName())
            .dateStr(null)
            .category(xPathFetcherConfig.getName())
            .build();

    currentWeb.add(webContent);



    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", xPathFetcherConfig.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知", xPathFetcherConfig.getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

}