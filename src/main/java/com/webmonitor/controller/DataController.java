package com.webmonitor.controller;

import com.webmonitor.entity.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/data")
public class DataController {

  @GetMapping("/ai/demo")
  public ResponseVO<List<String>> chatWithAI() {
    return ResponseVO.success(Arrays.asList("每10分钟获取百度热搜第一名",
            "每小时获取北京天气信息",
            "监控指定股票价格变化",
            "每天获取最新科技新闻"));
  }
}