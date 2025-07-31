package com.webmonitor.util;

import cn.hutool.core.collection.CollectionUtil;
import com.webmonitor.constant.AIModelEnum;
import com.webmonitor.constant.SelectorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.model.ChatModel;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
public class JsoupUtil {
  public static final String XPATH_PROMPT_TXT = "prompts/xpath_generator_prompt.txt";
  public static final String CSS_PROMPT_TXT = "prompts/css_generator_prompt.txt";


  public static Map<String, String> getByCssSelector(String url, Map<String, String> selectorDict,
                                                     Map<String, String> headers, String cookie) throws IOException {
    String html = HtmlUtil.getHtmlByJsoup(url, headers,  cookie);
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

      System.out.println(HtmlUtil.cleanHtml( html));
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
      throw new RuntimeException("css 解析失败: " + e);
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

  public static String getXPathFromAI(String url, String modelName, String userQuery,
                                      Map<AIModelEnum, ChatModel> aiModelMap) throws Exception {
    return getSelectorFromAI(url, modelName, userQuery, aiModelMap, SelectorTypeEnum.XPATH);
  }

  public static String getCssSelectorFromAI(String url, String modelName, String userQuery,
                                            Map<AIModelEnum, ChatModel> aiModelMap) throws Exception {
    return getSelectorFromAI(url, modelName, userQuery, aiModelMap, SelectorTypeEnum.CSS);
  }


  private static String getSelectorFromAI(String url, String modelName, String userQuery,
                                          Map<AIModelEnum, ChatModel> aiModelMap, SelectorTypeEnum typeEnum) throws Exception {
    // 获取网页内容
    String html = HtmlUtil.getHtmlByJsoup(url, null, null);
    // todo
    html = HtmlUtil.getHtmlBySelenium(url);
    String cleanedHtml = HtmlUtil.cleanHtml(html);


    String prompt = AIUtil.getPrompt(userQuery, typeEnum == SelectorTypeEnum.CSS ? CSS_PROMPT_TXT : XPATH_PROMPT_TXT, cleanedHtml);

    log.info("[getSelectorFromAI] Start call api");
    String selectorFromAI = AIUtil.callAI(modelName, aiModelMap, prompt);
    if (selectorFromAI == null || selectorFromAI.isEmpty()) {
      throw new Exception("通过ai获取" + typeEnum.getCode() +"失败，请检查配置，或者重试，selector ：" + selectorFromAI);
    }
    selectorFromAI = selectorFromAI.replace("`",   "");
    selectorFromAI = selectorFromAI.replace("xpath",   "");
    // 去掉换行
    selectorFromAI = selectorFromAI.replace("\n", "");
    log.info("[getSelectorFromAI] 成功通过ai获取" + typeEnum.getCode() + ": {}", selectorFromAI);
    return selectorFromAI + "|text";
  }

  public static Element getContentDocumentByKeyWord(Document document, String keyword) throws Exception {
    Elements elements = document.getElementsContainingOwnText(keyword);
    // todo
    Elements elements2 = document.getElementsContainingText(keyword);
//    Elements elementsMatchingText = document.getElementsMatchingText(".*"+ config.getKeyword() +".*");
    if (CollectionUtil.isEmpty(elements)) {
      return null;
    }
    return elements.first();
  }

}
