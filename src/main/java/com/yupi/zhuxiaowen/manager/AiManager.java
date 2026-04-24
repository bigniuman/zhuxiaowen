package com.yupi.zhuxiaowen.manager;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.yupi.zhuxiaowen.common.ErrorCode;
import com.yupi.zhuxiaowen.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AiManager {
    @Resource
    private ZhipuAiClient client;

    private static final float STABLE_TEMPERATURE = 0.05f;
    private static final float UNSTABLE_TEMPERATURE = 0.99f;
    public String doSyncUnstableRequest(String userMessage,String systemMessage){
        return doRequest(userMessage,systemMessage,false,UNSTABLE_TEMPERATURE);
    }
    public String doSyncStableRequest(String userMessage,String systemMessage){
        return doRequest(userMessage,systemMessage,false,STABLE_TEMPERATURE);
    }
    public String doSyncRequest(String userMessage,String systemMessage,float temperature){
        return doRequest(userMessage,systemMessage,false,temperature);
    }
    public String doRequest(String userMessage,String systemMessage,boolean stream,float temperature){
        List<ChatMessage> chatMessagesList=new ArrayList<>();
        ChatMessage ChatSystemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessagesList.add(ChatSystemMessage);
        ChatMessage ChatUserMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessagesList.add(ChatUserMessage);
        return doRequest(chatMessagesList,stream,temperature);
    }

    public String doRequest(List<ChatMessage> messages,boolean stream,float temperature){
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-4.6v")
                .messages(messages)
                .stream(stream)
                .temperature(temperature)
                .maxTokens(1024)
                .build();
        try {
            // 发送请求
            ChatCompletionResponse response = client.chat().createChatCompletion(request);
            return response.getData().getChoices().get(0).toString();
            // 获取回复
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,e.getMessage());
        }
    }

}
