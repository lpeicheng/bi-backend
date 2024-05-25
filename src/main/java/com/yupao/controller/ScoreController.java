package com.yupao.controller;


import com.yupao.common.BaseResponse;
import com.yupao.common.ResultUtils;
import com.yupao.model.entity.User;
import com.yupao.service.ScoreService;
import com.yupao.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 积分系统
 */
@RestController
@RequestMapping("/score")
public class ScoreController {
    @Resource
    private UserService userService;

    @Resource
    private ScoreService scoreService;

    /**
     * 用于签到时添加积分
     *
     * @param request 请求
     * @return 签到成功
     */
    @PostMapping("/checkIn")
    public BaseResponse<String> checkIn(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        scoreService.checkIn(loginUser.getId());
        return ResultUtils.success("签到成功");
    }

    /**
     * 查询积分
     *
     * @param request 请求
     * @return 积分总数
     */
    @GetMapping("/get")
    public BaseResponse<Long> getUserById(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long totalPoints = scoreService.getUserScore(loginUser.getId());
        return ResultUtils.success(totalPoints);
    }

    /**
     * 查询签到状态
     *
     * @param request 请求
     * @return 签到状态
     */
    @GetMapping("/getSign")
    public BaseResponse<Integer> getSignById(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int isSign = scoreService.getIsSign(loginUser.getId());
        return ResultUtils.success(isSign);
    }

    /**
     * 查询消耗tokens
     *
     * @param request 请求
     * @return 消耗tokens
     */
    @GetMapping("/getTokens")
    public BaseResponse<Long> getTokens(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long tokens = scoreService.getUserTokens(loginUser.getId());
        return ResultUtils.success(tokens);
    }
}


