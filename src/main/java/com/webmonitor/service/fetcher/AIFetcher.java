package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.AIFetcherConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.constant.SelectorTypeEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.HtmlUtil;
import com.webmonitor.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.model.ChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class AIFetcher implements ContentFetcher {
  private final AIFetcherConfig aiFetcherConfig;
  private String cachedSelector;
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private Map<AIModelEnum, ChatModel> aiModelMap;



  public AIFetcher(AIFetcherConfig config, Map<AIModelEnum, ChatModel> aiModelMap) {
    this.aiFetcherConfig = config;
    this.aiModelMap = aiModelMap;

  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", aiFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", aiFetcherConfig.getName());
    }

    // 检查是否有缓存的
    if (cachedSelector == null) {
      if (SelectorTypeEnum.CSS.getCode().equals(aiFetcherConfig.getSelectorType())){
        cachedSelector = JsoupUtil.getXPathFromAI(aiFetcherConfig.getUrl(), aiFetcherConfig.getModelName(),
                aiFetcherConfig.getUserQuery(), aiModelMap);
      } else {
        cachedSelector = JsoupUtil.getCssSelectorFromAI(aiFetcherConfig.getUrl(), aiFetcherConfig.getModelName(),
                aiFetcherConfig.getUserQuery(), aiModelMap);
      }
    }

    Document document = HtmlUtil.getDocument(aiFetcherConfig.getUrl(), null, aiFetcherConfig.getCookie());
    String html = document.html();

    String title = "";
    if (SelectorTypeEnum.CSS.getCode().equals(aiFetcherConfig.getSelectorType())) {
      title = JsoupUtil.cssParse(html, cachedSelector);
    } else {
      title = JsoupUtil.xpathParse(html, cachedSelector);
    }

    log.info("{}获取到内容：{}", aiFetcherConfig.getName(), title);
    WebContent webContent = WebContent.builder()
            .id(title)
            .title(title)
            .description(title)
            .link(null)
            .source(aiFetcherConfig.getName())
            .dateStr(null)
            .category(aiFetcherConfig.getName())
            .build();

    List<WebContent> currentWeb = new ArrayList<>();
    currentWeb.add(webContent);

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", aiFetcherConfig.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条内容，不通知", aiFetcherConfig.getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }



}