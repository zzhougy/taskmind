package com.webmonitor.service.fetcher;

import com.webmonitor.config.fetcher.AIMcpFetcherConfig;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.core.ContentFetcher;
import com.webmonitor.core.WebContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.webmonitor.util.AIUtil.getPrompt;

@Slf4j
public class AIMcpFetcher implements ContentFetcher {

  public static final String PLAYWRIGHT_PROMPT_TXT = "prompts/playwright_generator_prompt.txt";

  private final AIMcpFetcherConfig config;
  private List<WebContent> lastWeb = new ArrayList<>();
  private boolean isFirstLoad = true;
  private Map<AIModelEnum, ChatModel> aiModelMap;
  private ToolCallbackProvider tools;
  private AIModelEnum modelEnum;



  public AIMcpFetcher(AIMcpFetcherConfig config, Map<AIModelEnum, ChatModel> aiModelMap, ToolCallbackProvider tools) {
    this.config = config;
    this.aiModelMap = aiModelMap;
    this.tools = tools;
    this.modelEnum = AIModelEnum.getByName(config.getModelName());
    if (this.modelEnum == null) {
      throw new IllegalArgumentException("Invalid model name: " + config.getModelName());
    }
  }

  @Override
  public List<WebContent> fetch() throws Exception {
    if (isFirstLoad) {
      log.info("开始监控{}...", config.getName());
    } else {
      log.info("正在检查{}更新...", config.getName());
    }


    ListOutputConverter listConverter = new ListOutputConverter(new DefaultConversionService());
    String prompt = getPrompt((config.getUrl() == null ? "" : config.getUrl()) + config.getUserQuery(), PLAYWRIGHT_PROMPT_TXT);
    ChatClient.Builder builder = ChatClient.builder(aiModelMap.get(modelEnum));
    var chatClient = builder
            .defaultToolCallbacks(tools)
            .build();
    List<String> entity = chatClient.prompt(prompt).call().entity(listConverter);
    String join = String.join("、", entity);


    log.info("{}获取到内容：{}", config.getName(), entity);
    WebContent webContent = WebContent.builder()
            .id(join)
            .title(join)
            .description(join)
            .url(null)
            .source(config.getName())
            .dateStr(null)
            .category(config.getName())
            .build();

    List<WebContent> currentWeb = new ArrayList<>();
    currentWeb.add(webContent);

    List<WebContent> newWeb = new ArrayList<>();
    if (!isFirstLoad) {
      newWeb = findNewWebContent(currentWeb, lastWeb);
      log.info("{}检查完成，发现 {} 条新内容", config.getName(), newWeb.size());
    } else {
      log.info("首次加载{}，获取到 {} 条内容，不通知", config.getName(), currentWeb.size());
    }

    lastWeb = currentWeb;
    isFirstLoad = false;
    return newWeb;
  }



}