package com.webmonitor.controller.test;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.constant.AIModelEnum;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientAttributes;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController("/test/ai")
public class AITestController {

  @Autowired
  List<McpAsyncClient> mcpAsyncClients;
  // todo test
  @Resource
  private ToolCallbackProvider tools;
  @Resource
  private WebMonitorFactory webMonitorFactory;

  // todo remove
  @GuestAccess
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




  // todo  remove
  @GuestAccess
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

    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU));
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

  // todo  remove
  @GuestAccess
  @GetMapping("/outputConverter")
  public List<String> chatList(@RequestParam(value = "query", defaultValue = "请为我描述下影子的特性") String query) {
    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU));
    var chatClient = builder
            .build();
    ListOutputConverter listConverter = new ListOutputConverter(new DefaultConversionService());
    return chatClient.prompt(query)
            .advisors(
                    a -> a.param(ChatClientAttributes.OUTPUT_FORMAT.getKey(), listConverter.getFormat())
            ).call().entity(listConverter);
  }

  // todo
  @GuestAccess
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