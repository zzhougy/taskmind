package com.webmonitor.util;

import cn.hutool.extra.spring.SpringUtil;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;

public class SensitiveUtil {

  // 过滤敏感词
  public static String filterSensitiveWords(String content) {
    SensitiveWordBs bean = SpringUtil.getBean(SensitiveWordBs.class);
    content = bean.replace(content);
    return content;
  }

  public static void main(String[] args) {
    SensitiveWordBs wordBs = SensitiveWordBs.newInstance().init();
    String result = wordBs.replace("");
    System.out.println(result);
  }

}
