package com.yupao.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户获取验证码请求
 */
@Data
public class UserCaptchaRequest implements Serializable {

    /**
     * 用户邮箱
     */
    private String userEmail;

    private static final long serialVersionUID = 1L;
}
