package com.yupi.zhuxiaowen.model.dto.question;

import lombok.Data;
@Data
public class AiGenerateQuestionRequest {
    private long appId;
    int questionNumber=10;
    int optionNumber=2;
    private static final long serialVersionUID = 1L;
}
