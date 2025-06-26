package com.webmonitor.controller;

import com.webmonitor.config.WebMonitorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @Autowired
  private WebMonitorProperties webMonitorProperties;

  @GetMapping("/test")
  public String testApi() {
    System.out.println(webMonitorProperties.getConfigs());
    return "Hello, this is a test API!";
  }
}