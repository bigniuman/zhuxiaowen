package com.yupi.zhuxiaowen.scoring;

import com.yupi.zhuxiaowen.model.entity.App;
import com.yupi.zhuxiaowen.model.entity.UserAnswer;

import java.util.List;

public interface ScoringStrategy {

    /**
     * 执行评分
     *
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}
