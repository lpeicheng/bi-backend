package com.yupao.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao.common.ErrorCode;
import com.yupao.exception.ThrowUtils;
import com.yupao.mapper.ScoreMapper;
import com.yupao.model.entity.Score;
import com.yupao.service.ScoreService;
import org.springframework.stereotype.Service;

/**
 * 积分实现类
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    @Override
    public void checkIn(Long userId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Score score = this.getOne(queryWrapper);
        ThrowUtils.throwIf(score == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(score.getIsSign() == 1, ErrorCode.PARAMS_ERROR, "领取失败，今日已领取");
        Long scoreTotal = score.getScoreTotal();
        UpdateWrapper<Score> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                //此处暂时写死签到积分
                .eq("userId", userId)
                .set("scoreTotal", scoreTotal + 1)
                .set("isSign", 1);
        boolean r = this.update(updateWrapper);
        ThrowUtils.throwIf(!r, ErrorCode.OPERATION_ERROR, "更新签到数据失败");
    }

    @Override
    public void deductScore(Long userId, Long points) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Score score = this.getOne(queryWrapper);
        ThrowUtils.throwIf(score.getScoreTotal() < points, ErrorCode.OPERATION_ERROR, "积分不足，请联系管理员！");
        Long scoreTotal = score.getScoreTotal();
        UpdateWrapper<Score> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("userId", userId)
                .set("scoreTotal", scoreTotal - points);
        boolean r = this.update(updateWrapper);
        ThrowUtils.throwIf(!r, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public Long getUserScore(Long userId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Score score = this.getOne(queryWrapper);
        return score.getScoreTotal();
    }


    @Override
    public void depleteTokens(Long userId, Long tokens) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Score score = this.getOne(queryWrapper);
        Long depleteTokens = score.getTokens();
        UpdateWrapper<Score> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId).set("tokens", depleteTokens + tokens);
        boolean updateResult = this.update(updateWrapper);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public int getIsSign(Long userId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Score score = this.getOne(queryWrapper);
        return score.getIsSign();
    }

    @Override
    public Long getUserTokens(Long userId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Score score = this.getOne(queryWrapper);
        return score.getTokens();
    }
}




