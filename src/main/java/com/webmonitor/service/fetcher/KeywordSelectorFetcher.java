package com.webmonitor.service.fetcher;

import com.webmonitor.config.exception.SystemException;
import com.webmonitor.config.fetcher.KeywordSelectorFetcherConfig;
import com.webmonitor.constant.WayToGetHtmlEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.HtmlUtil;
import com.webmonitor.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class KeywordSelectorFetcher implements ContentFetcher {
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private final KeywordSelectorFetcherConfig config;

  public KeywordSelectorFetcher(KeywordSelectorFetcherConfig config) {
    this.config = config;
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", config.getName());
    } else {
      log.info("正在检查{}更新...", config.getName());
    }

    List<WebContent> currentWeb = new ArrayList<>();

    Document document = HtmlUtil.getDocumentByWayToGetHtml(config.getUrl(), WayToGetHtmlEnum.getByCode(config.getWayToGetHtml()));

    Element element = JsoupUtil.getContentDocumentByKeyWord(document, config.getKeyword());
    if (element == null || StringUtils.isEmpty(element.text())) {
      throw new SystemException(config.getName() + "没有获取到内容，请重试或者联系管理员");
    }
    String contentByKeyWord = element.text();
    log.info("{}获取到内容：{}", config.getName(), contentByKeyWord);
    WebContent webContent = WebContent.builder()
            .id(contentByKeyWord)
            .title(contentByKeyWord)
            .description(contentByKeyWord)
            .url(null)
            .source(config.getName())
            .dateStr(null)
            .category(config.getName())
            .build();

    currentWeb.add(webContent);


    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", config.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条政策，不通知。内容如下：{}", config.getName(), currentWeb.size(), currentWeb);
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }

}