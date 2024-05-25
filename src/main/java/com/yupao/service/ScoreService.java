package com.yupao.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupao.model.entity.Score;

/**
 * 积分服务类
 */
public interface ScoreService extends IService<Score> {
    /**
     * 签到
     *
     * @param userId 用户id
     */
    void checkIn(Long userId);

    /**
     * 消耗积分
     *
     * @param userId 用户id
     * @param points 积分数
     */
    void deductScore(Long userId, Long points);


    /**
     * 用户消耗Tokens
     *
     * @param userId 用户id
     * @param tokens 消耗积分数
     */
    void depleteTokens(Long userId, Long tokens);

    /**
     * 获取积分
     *
     * @param userId 用户id
     * @return 积分总数
     */
    Long getUserScore(Long userId);

    /**
     * 获取是否签到状态
     *
     * @param userId 用户id
     * @return 签到状态
     */
    int getIsSign(Long userId);

    Long getUserTokens(Long userId);
}
