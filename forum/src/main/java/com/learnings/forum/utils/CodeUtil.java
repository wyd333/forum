package com.learnings.forum.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description: 验证码工具
 * User: 12569
 * Date: 2023-12-02
 * Time: 14:16
 */
public class CodeUtil {
    public static final int CODE_LENGTH = 6;

    /**
     * 生成 CODE_LENGTH 长度的随机验证码
     * @return 生成的验证码
     */
    public String createCode() {
        String charset = "0123456789"; // 可以包含在验证码中的字符集合
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(charset.length());
            code.append(charset.charAt(randomIndex));
        }
        return code.toString();
    }
}
