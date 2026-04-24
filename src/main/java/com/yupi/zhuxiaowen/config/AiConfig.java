package com.yupi.zhuxiaowen.config;

import ai.z.openapi.ZhipuAiClient;
import ch.qos.logback.core.net.server.Client;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {
    private String apiKey;
    @Bean
    public ZhipuAiClient getZhipuAiClient() {
        return ZhipuAiClient.builder().ofZHIPU()
                .apiKey(apiKey)
                .build();
    }
}
