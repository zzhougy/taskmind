package com.webmonitor.util;

import com.webmonitor.constant.AIModelEnum;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.model.ChatModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
public class JsoupUtil {
  public static final String PROMPT_TXT = "prompts/xpath_generator_prompt.txt";


  public static Map<String, String> getByCssSelector(String url, Map<String, String> selectorDict,
                                                     Map<String, String> headers, String cookie) throws IOException {
    String html = HtmlUtil.getHtml(url, headers,  cookie);
    System.out.println( html);
    Map<String, String> result = new LinkedHashMap<>();

    for (Map.Entry<String, String> entry : selectorDict.entrySet()) {
      String key = entry.getKey();
      String cssSelector = entry.getValue();

      String value = cssParse(html, cssSelector);
      result.put(key, value);
    }

    return result;
  }

  public static String cssParse(String html, String cssSelectorFull) {
    try {
      Document document = Jsoup.parse(html);

      // 分割自定义选择器
      String[] parts = StringUtil.splitAndCheckSelectorStr(cssSelectorFull);
      String cssSelector = parts[0];
      String attributePart = parts[1];

      Elements elements = document.select(cssSelector);

      if (!elements.isEmpty()) {
        Element first = elements.first();
        if ("text".equals(attributePart)) {
          return first.text(); // 获取文本内容
        } else {
          return first.attr(StringUtil.getAttribute(attributePart)); // 获取指定属性值
        }
      } else {
        throw new RuntimeException("未找到指定元素");
      }

    } catch (Exception e) {
      throw new RuntimeException("css 解析失败: " + e.getMessage());
    }
  }


  public static String xpathParse(String html, String xpath) {
    try {
      Document document = Jsoup.parse(html);

      // 分割自定义选择器
      String[] parts = StringUtil.splitAndCheckSelectorStr(xpath);
      String xPathSelector = parts[0];
      String attributePart = parts[1];

      Elements elements = document.selectXpath(xPathSelector);
      if (!elements.isEmpty()) {
        Element first = elements.first();
        if ("text".equals(attributePart)) {
          return first.text(); // 获取文本内容
        } else {
          return first.attr(StringUtil.getAttribute(attributePart)); // 获取指定属性值
        }
      } else {
        throw new RuntimeException("未找到指定元素");
      }
    } catch (Exception e) {
      throw new RuntimeException("xpath 解析失败: " + e.getMessage());
    }
  }



  public static String getXPathFromAI(String url, String modelName, String userQuery, Map<AIModelEnum, ChatModel> aiModelMap) throws Exception {
    // 获取网页内容
    String html = HtmlUtil.getHtml(url, null, null);
    String cleanedHtml = HtmlUtil.extractBodyByJsoup(html);

    // 读取prompt模板
    InputStream inputStream = JsoupUtil.class.getClassLoader().getResourceAsStream(PROMPT_TXT);
    if (inputStream == null) {
      throw new IOException("Prompt file not found");
    }
    String promptTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    String prompt = promptTemplate.replace("用户本次需求：", "用户本次需求：" + userQuery)
            .replace("HTML 内容如下：", "HTML 内容如下：" + cleanedHtml);
    String xpathFromAI = AIUtil.callAI(modelName, aiModelMap, prompt);
    if (xpathFromAI == null || xpathFromAI.isEmpty()) {
      throw new Exception("通过ai获取XPath失败，请检查配置，或者重试，cachedXPath ：" + xpathFromAI);
    }
    xpathFromAI = xpathFromAI.replace("`",   "");
    xpathFromAI = xpathFromAI.replace("xpath",   "");
    // 去掉换行
    xpathFromAI = xpathFromAI.replace("\n", "");
    log.info("成功通过ai获取XPath: {}", xpathFromAI);
    return xpathFromAI + "|text";
  }




}
