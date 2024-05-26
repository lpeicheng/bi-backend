package com.yupao.controller;

import com.yupao.common.BaseResponse;
import com.yupao.common.ResultUtils;
import com.yupao.model.dto.user.UserCaptchaRequest;
import com.yupao.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码接口
 *
 * @author paopao
 * @Date 2024/5/3
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Resource
    private UserService userService;

    /**
     * 邮箱注册验证码接口
     *
     * @param userCaptchaRequest 获取验证码请求
     */
    @PostMapping("/register")
    public BaseResponse<Boolean> sendRegisterCaptcha(@RequestBody UserCaptchaRequest userCaptchaRequest) {
        String userEmail = userCaptchaRequest.getUserEmail();
        boolean result = userService.sendRegisterCaptcha(userEmail);
        return ResultUtils.success(result);
    }

    /**
     * 忘记密码验证码接口
     *
     * @param userCaptchaRequest 获取验证码请求
     */
    @PostMapping("/forget")
    public BaseResponse<Boolean> sendForgetCaptcha(@RequestBody UserCaptchaRequest userCaptchaRequest) {
        String userEmail = userCaptchaRequest.getUserEmail();
        boolean result = userService.sendForgetCaptcha(userEmail);
        return ResultUtils.success(result);
    }
}
