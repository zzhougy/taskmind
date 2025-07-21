package com.webmonitor.controller.test;

import com.webmonitor.config.WebMonitorFactory;
import com.webmonitor.config.annotation.GuestAccess;
import com.webmonitor.constant.AIModelEnum;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @RequestMapping("/mcp")
  public void tes2t() {
    ChatClient.Builder builder = ChatClient.builder(webMonitorFactory.loadAIModels().get(AIModelEnum.ZHIPU));
    var chatClient = builder
            .defaultToolCallbacks(tools)
            .build();

    String userInput = "{\n" +
            "  \"cityName\": \"北京市\",\n" +
            "  \"forecastType\": \"current\"\n" +
            "}";
    System.out.println(" QUESTION: " + userInput);

    ChatClient.CallResponseSpec call = chatClient.prompt(userInput).call();
    ChatResponse chatResponse = call.chatResponse();
    System.out.println(" ASSISTANT: " +
            chatClient.prompt(userInput).call().content());
  }





}