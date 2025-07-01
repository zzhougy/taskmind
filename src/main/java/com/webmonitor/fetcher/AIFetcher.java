package com.webmonitor.fetcher;

import com.webmonitor.config.fetcher.AIFetcherConfig;
import com.webmonitor.constant.AIModelEnum;
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
import java.util.Map;

@Slf4j
public class AIFetcher implements ContentFetcher {
  public static final String PROMPT_TXT = "prompts/xpath_generator_prompt.txt";
  private final AIFetcherConfig aiFetcherConfig;
  private String cachedXPath;
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

    // 检查是否有缓存的XPath
    if (cachedXPath == null) {
      cachedXPath = getXPathFromAI();
      if (cachedXPath == null || cachedXPath.isEmpty()) {
        throw new Exception("通过ai获取XPath失败，请检查配置，或者重试，cachedXPath ：" + cachedXPath);
      }
      cachedXPath = cachedXPath.replace("`",   "");
      cachedXPath = cachedXPath.replace("xpath",   "");
      // 去掉换行
      cachedXPath = cachedXPath.replace("\n", "");
      cachedXPath = cachedXPath + "|text";
      log.info("成功通过ai获取XPath: {}", cachedXPath);
    }

    // 使用缓存的XPath获取内容
    Document document = HtmlUtil.getDocument(aiFetcherConfig.getUrl(), null, aiFetcherConfig.getCookie());
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

  private String getXPathFromAI() throws Exception {
    // 获取网页内容
    String html = HtmlUtil.getHtml(aiFetcherConfig.getUrl(), null, aiFetcherConfig.getCookie());
    String cleanedHtml = HtmlUtil.extractBodyByJsoup(html);

    // 读取prompt模板
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROMPT_TXT);
    if (inputStream == null) {
      throw new IOException("Prompt file not found");
    }
    String promptTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    String prompt = promptTemplate.replace("用户本次需求：", "用户本次需求：" + aiFetcherConfig.getUserQuery())
            .replace("HTML 内容如下：", "HTML 内容如下：" + cleanedHtml);
    String modelName = aiFetcherConfig.getModelName();
    if (AIModelEnum.ZHIPU.getName().equals(modelName) && aiModelMap.get(AIModelEnum.ZHIPU) != null) {
      return aiModelMap.get(AIModelEnum.ZHIPU).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.KIMI.getName().equals(modelName) && aiModelMap.get(AIModelEnum.KIMI) != null) {
      return aiModelMap.get(AIModelEnum.KIMI).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else if (AIModelEnum.CUSTOM.getName().equals(modelName) && aiModelMap.get(AIModelEnum.CUSTOM) != null) {
      return aiModelMap.get(AIModelEnum.CUSTOM).call(new Prompt(prompt)).getResult().getOutput().getText();
    } else {
      throw new Exception("不支持的模型：" + modelName);
    }
  }

}