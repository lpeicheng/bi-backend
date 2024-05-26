package com.yupao.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 生成验证码工具类
 */
public class CaptchaGenerateUtil {
    //验证码包含的字段
    private static final String SYMBOLS = "0123456789";
    private static final Random RANDOM = new SecureRandom();

    //生成 6 位数的随机数字
    public static String generateVerCode() {
        //生成大小为 6 的数组
        char[] numbers = new char[6];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(numbers);
    }
}
