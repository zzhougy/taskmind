package com.webmonitor.fetcher;

import com.webmonitor.config.fetcher.AIFetcherConfig;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import com.webmonitor.util.HtmlUtil;
import com.webmonitor.util.JsoupUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AIFetcher implements ContentFetcher {
  public static final String PROMPT_TXT = "prompts/xpath_generator_prompt.txt";
  private final AIFetcherConfig aiFetcherConfig;
  private String cachedXPath;
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private ChatModel zhipuAiChatModel;



  public AIFetcher(AIFetcherConfig config, ChatModel zhipuAiChatModel) {
    this.aiFetcherConfig = config;
    this.zhipuAiChatModel = zhipuAiChatModel;

  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", aiFetcherConfig.getName());
    } else {
      log.info("正在检查{}更新...", aiFetcherConfig.getName());
    }

    // 检查是否有缓存的XPath
    if (cachedXPath == null) {
      cachedXPath = getXPathFromAI();
      cachedXPath = cachedXPath + "|text";
      log.info("成功通过ai获取XPath: {}", cachedXPath);
    }

    // 使用缓存的XPath获取内容
    Document document = HtmlUtil.getDocument(aiFetcherConfig.getUrl(), null);
    String html = document.html();
    String title = JsoupUtil.xpathParse(html, cachedXPath);

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

  private String getXPathFromAI() throws IOException {
    // 获取网页内容
    String html = HtmlUtil.getHtml(aiFetcherConfig.getUrl(), null);
    String cleanedHtml = HtmlUtil.extractBodyByJsoup(html);

    // 读取prompt模板
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROMPT_TXT);
    if (inputStream == null) {
      throw new IOException("Prompt file not found");
    }
    String promptTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    String prompt = promptTemplate.replace("用户本次需求：", "用户本次需求：" + aiFetcherConfig.getUserQuery()) + cleanedHtml;

    String xpath = zhipuAiChatModel.call(new Prompt(prompt)).getResult().getOutput().getText();


    return xpath;
  }

}