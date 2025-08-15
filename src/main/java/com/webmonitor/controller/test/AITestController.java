package com.webmonitor.controller.test;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.constant.AIModelEnum;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientAttributes;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.webmonitor.util.AIUtil.getPrompt;

@Slf4j
@RestController("/test/ai")
public class AITestController {

  public static final String PLAYWRIGHT_PROMPT_TXT = "prompts/playwright_generator_prompt.txt";

  @Autowired
  List<McpAsyncClient> mcpAsyncClients;
  // todo test
  @Resource
  private ToolCallbackProvider tools;
  @Resource
  private WebMonitorFactory webMonitorFactory;


  @RequestMapping("/mcp/sse")
  public void test() {
    var mcpClient = mcpAsyncClients.get(0);

    Mono<McpSchema.CallToolResult> callToolResultMono = mcpClient.listTools()
            .flatMap(tools -> {
              log.info("tools: {}", tools);

              return mcpClient.callTool(
                      new McpSchema.CallToolRequest(
                              "maps_weather",
                              Map.of("city", "北京")
                      )
              );
            });
    log.info("callToolResultMono: {}", callToolResultMono.block().content());
  }




  @RequestMapping("/mcp/stdio")
  public void tes2t() {
//    String userInput = "{\n" +
//            "  \"cityName\": \"北京市\",\n" +
//            "  \"forecastType\": \"current\"\n" +
//            "}";

    String prompt = "北京未来7天的天气";
//        ChatClient.CallResponseSpec call = ChatClient.create(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU))
//                .prompt(prompt)
//                .toolCallbacks(tools)
//                .toolContext(Map.of("userInput", bo.getUserInput()))
//                .call();

    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU_GLM4_FLASH));
    var chatClient = builder
            .defaultToolCallbacks(tools)
            .build();
    ChatClient.CallResponseSpec call = chatClient.prompt(prompt).call();


//        ToolCallback[] dateTimeTools = ToolCallbacks.from(new TaskTools());
//        ChatOptions chatOptions = ToolCallingChatOptions.builder()
//                .toolCallbacks(dateTimeTools)
//                .build():
//        SyncMcpToolCallbackProvider syncMcpToolCallbackProvider = new SyncMcpToolCallbackProvider();

//    ChatClientResponse chatClientResponse = call.chatClientResponse();
//    ChatResponse chatResponse = call.chatResponse();
    String content = call.content();
    log.info("AI Response: {}", content);

  }

  /**
   * 测试playwright
   * @param userQuery
   * @throws IOException
   */
  @RequestMapping("/mcp/stdio/playwright")
  public void tes2t(@RequestParam(value = "userQuery", defaultValue = "打开b站然后搜索java，点击第一个视频播放") String userQuery) throws IOException {
    ListOutputConverter listConverter = new ListOutputConverter(new DefaultConversionService());
    String prompt = getPrompt(userQuery, PLAYWRIGHT_PROMPT_TXT);
    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU_GLM45_FLASH));
    var chatClient = builder
            .defaultToolCallbacks(tools)
            .build();
    List<String> entity = chatClient.prompt(prompt).call().entity(listConverter);

    log.info("AI Response: {}", entity);

  }

  @GetMapping("/mcp/stdio/playwright/stream")
  public List<String> testStr222eam(
          @RequestParam(value = "userQuery",
                  defaultValue = "1、打开b站然后搜索java，2、点击第一个视频播放")
          String userQuery) throws IOException {

    String prompt = getPrompt(userQuery, PLAYWRIGHT_PROMPT_TXT);
    ChatClient chatClient = ChatClient.builder(
                    webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU_GLM45_FLASH))
            .defaultToolCallbacks(tools)
            .build();
//
//    /* 1. 后台线程订阅流，仅打印，不影响主流程 */
//    chatClient.prompt(prompt)
//            .stream()
//            .content()
//            .doOnNext(chunk -> log.info("Partial chunk: {}", chunk))
//            .doOnError(err -> log.error("Stream error", err))
//            .subscribe();   // 不阻塞
//
//    /* 2. 同步调用，直接返回完整结果 */
//    List<String> entity = chatClient.prompt(prompt)
//            .call()
//            .entity(new ListOutputConverter(new DefaultConversionService()));


    List<String> result = new ArrayList<>();
    chatClient.prompt(prompt)
            .stream()
            .content()
            .doOnNext(chunk -> log.info("Partial chunk: {}", chunk))
            .doOnError(err -> log.error("Stream error", err))
            .doOnNext(result::add) // 收集中间结果
            .blockLast(); // 阻塞等待流结束


    log.info("AI Response: {}", result);
    return result;
  }


  @RequestMapping("/image/explain")
  public void tes23t(@RequestParam(value = "userQuery", defaultValue = "解释一下你在这幅图上看到了什么?") String userQuery) throws IOException, URISyntaxException {
    var userMessage = UserMessage.builder()
//            .text("Explain what do you see on this picture?")
            .text(userQuery)
            .media(List.of(Media.builder()
                    .mimeType(MimeTypeUtils.IMAGE_PNG)
//                    .data(URI.create("https://docs.spring.io/spring-ai/reference/_images/multimodal.test.png"))
                    .data(new ClassPathResource("Snipaste_2025-08-10_20-59-31.png"))
                    .build()))
            .build();


    ChatModel chatModel = webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU_GLM4V_FLASH);
    Flux<ChatResponse> response = chatModel.stream(new Prompt(List.of(userMessage),
            ZhiPuAiChatOptions.builder().build()));

    String content = Objects.requireNonNull(response.collectList().block())
            .stream()
            .map(ChatResponse::getResults)
            .flatMap(List::stream)
            .map(Generation::getOutput)
            .map(AssistantMessage::getText)
            .collect(Collectors.joining());
//    assertThat(content).containsAnyOf("bananas", "apple", "bowl", "basket", "fruit stand");
    log.info("AI Response: {}", content);

  }

  @RequestMapping("/image/generate")
  public void tes23tcreate(@RequestParam(value = "userInput", defaultValue = "画一朵花") String userInput) {
    ImageModel imageModel = webMonitorFactory.loadAIImageModels().get(AIModelEnum.ZHIPU_COGVIEW3_FLASH);
    ImageResponse response = imageModel.call(
            new ImagePrompt(userInput, null));
    for (ImageGeneration result : response.getResults()) {
      Image output = result.getOutput();
      System.out.println("Image URL: " + output.getUrl());
    }
  }


  @GetMapping("/outputConverter")
  public List<String> chatList(@RequestParam(value = "query", defaultValue = "请为我描述下影子的特性") String query) {
    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU_GLM45_FLASH));
    var chatClient = builder
            .build();
    ListOutputConverter listConverter = new ListOutputConverter(new DefaultConversionService());
    return chatClient.prompt(query)
            .advisors(
                    a -> a.param(ChatClientAttributes.OUTPUT_FORMAT.getKey(), listConverter.getFormat())
            ).call().entity(listConverter);
  }

  @RequestMapping("/simple/chat")
  public void testModel() {
    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory
            .loadAIModels().get(AIModelEnum.KIMI));
    var chatClient = builder.build();
    String userInput = "https://baijiahao.baidu.com/s?id=1838712933161907631&wfr=spider&for=pc   " +
            "总结";
    System.out.println(" QUESTION: " + userInput);
    System.out.println(" ASSISTANT: " +
            chatClient.prompt(userInput).call().content());
  }


}