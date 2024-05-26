package com.yupao.service.impl;

import com.yupao.common.ErrorCode;
import com.yupao.exception.BusinessException;
import com.yupao.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import org.thymeleaf.context.Context;
/**
 * 邮箱服务
 * @author paopao
 * @Date 2024/5/3
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Resource
    private JavaMailSender mailSender;

    @Resource
    TemplateEngine templateEngine;

    // 获取发件人邮箱
    @Value("${spring.mail.username}")
    private String sender;

    // 获取发件人昵称
    @Value("${spring.mail.nickname}")
    private String nickname;

    /**
     * 发送注册邮件
     *
     * @param userEmail 用户邮箱
     * @param captcha   随机验证码
     */
    @Override
    public void sendRegisterEmailCaptcha(String userEmail, String captcha) {
        Context context = new Context();
        context.setVariable("verifyCode", Arrays.asList(captcha.split("")));
        String emailContent = templateEngine.process("RegisterEmail", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setFrom(nickname + '<' + sender + '>');
            helper.setTo(userEmail);
            helper.setSubject("欢迎访问 BI 平台");
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱发送失败");
        }
    }

    /**
     * 发送注册邮件
     *
     * @param userEmail 用户邮箱
     * @param captcha   随机验证码
     */
    @Override
    public void sendForgetEmailCaptcha(String userEmail, String captcha) {
        Context context = new Context();
        context.setVariable("verifyCode", Arrays.asList(captcha.split("")));
        String emailContent = templateEngine.process("ForgetEmail", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setFrom(nickname + '<' + sender + '>');
            helper.setTo(userEmail);
            helper.setSubject("欢迎访问 BI 平台");
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱发送失败");
        }
    }
}
