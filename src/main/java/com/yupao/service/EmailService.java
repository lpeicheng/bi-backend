package com.yupao.service;

/**
 * @author paopao
 * @Date 2024/5/3
 */
public interface EmailService {
    void sendRegisterEmailCaptcha(String userEmail, String captcha);

    void sendForgetEmailCaptcha(String userEmail, String captcha);
}
