package com.yupi.zhuxiaowen;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ZhiPuAiTest {

    @Resource
    private ZhipuAiClient zhipuAiClient;
    @Test
    public void test() {
        ZhipuAiClient client = ZhipuAiClient.builder().ofZHIPU()
                .apiKey("a8fe1514fc334fceb7b8182ce285324b.kEzFJrhdbNyLNpEl")
                .build();

        // 创建聊天完成请求
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4.6v")
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content("Hello, who are you?")
                                .build()
                ))
                .stream(false)
                .temperature(0.6f)
                .maxTokens(1024)
                .build();

        // 发送请求
        ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(request);

        // 获取回复
        System.out.println(response.getData().getChoices().get(0).getMessage());
    }
}
