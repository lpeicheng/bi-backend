package com.yupao.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserForgetRequest implements Serializable {

    private String userEmail;

    private String userPassword;

    private String checkPassword;

    //验证码
    private String captcha;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
