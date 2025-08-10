package com.webmonitor.config;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ZzConfig {

  /**
   * 创建RestClient对象
   * 控制ai请求的超时时间
   * @param restClientBuilderConfigurer
   * @return
   */
  @Bean
  RestClient.Builder restClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
    RestClient.Builder builder = RestClient.builder()
            .requestFactory(ClientHttpRequestFactories.get(new ClientHttpRequestFactorySettings(Duration.ofMillis(10),
                    Duration.ofSeconds(60), (SslBundle)null)));
    return restClientBuilderConfigurer.configure(builder);
  }

}
