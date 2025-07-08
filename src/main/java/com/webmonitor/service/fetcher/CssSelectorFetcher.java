package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.CssSelectorFetcherConfig;
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
public class CssSelectorFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final CssSelectorFetcherConfig cssSelectorFetcherConfig;

  public CssSelectorFetcher(CssSelectorFetcherConfig cssSelectorFetcherConfig) {
    this.cssSelectorFetcherConfig = cssSelectorFetcherConfig;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", cssSelectorFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", cssSelectorFetcherConfig.getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();

    String title = null;
    if (cssSelectorFetcherConfig.getWayToGetHtml().equals(WayToGetHtmlEnum.JSOUP.getCode())) {
      Document document = HtmlUtil.getDocument(cssSelectorFetcherConfig.getUrl(), null, cssSelectorFetcherConfig.getCookie());
      title = JsoupUtil.cssParse(document.html(), cssSelectorFetcherConfig.getCssSelector());
    } else if (cssSelectorFetcherConfig.getWayToGetHtml().equals(WayToGetHtmlEnum.SELENIUM.getCode())) {
      title = JsoupUtil.cssParse(HtmlUtil.getHtmlBySelenium(cssSelectorFetcherConfig.getUrl()), cssSelectorFetcherConfig.getCssSelector());
    }

    WebContent webContent = WebContent.builder()
            .id(title)
            .title(title)
            .description(title)
            .url(null)
            .source(cssSelectorFetcherConfig.getName())
            .dateStr(null)
            .category(cssSelectorFetcherConfig.getName())
            .build();

    currentWeb.add(webContent);

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", cssSelectorFetcherConfig.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知。内容如下：{}", cssSelectorFetcherConfig.getName(), currentWeb.size(), currentWeb);
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

}