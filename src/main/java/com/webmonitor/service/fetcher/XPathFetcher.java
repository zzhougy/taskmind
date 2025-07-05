package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.XPathFetcherConfig;
import com.webmonitor.constant.WayToGetHtmlEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.HtmlUtil;
import com.webmonitor.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

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


    String title = null;
    if (xPathFetcherConfig.getWayToGetHtml().equals(WayToGetHtmlEnum.JSOUP.getCode())) {
      Document document = HtmlUtil.getDocument(xPathFetcherConfig.getUrl(), null, xPathFetcherConfig.getCookie());
      title = JsoupUtil.xpathParse(document.html(), xPathFetcherConfig.getXPath());
    } else if (xPathFetcherConfig.getWayToGetHtml().equals(WayToGetHtmlEnum.SELENIUM.getCode())) {
      title = JsoupUtil.xpathParse(HtmlUtil.getHtmlBySelenium(xPathFetcherConfig.getUrl()), xPathFetcherConfig.getXPath());
    }


    log.info("{}获取到内容：{}", xPathFetcherConfig.getName(), title);
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
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("首次加载{}，获取到 {} 条政策，不通知。内容如下：{}", xPathFetcherConfig.getName(), currentWeb.size(), currentWeb);
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

}