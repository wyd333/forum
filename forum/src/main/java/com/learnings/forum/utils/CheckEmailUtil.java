package com.learnings.forum.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description: 校验邮箱格式
 * User: 12569
 * Date: 2023-12-18
 * Time: 21:28
 */
public class CheckEmailUtil {
    public static Boolean validateEmail(String email) {
        // 邮箱验证的正则表达式
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 匹配邮箱
        Matcher matcher = pattern.matcher(email);

        // 如果不匹配，则返回false
        return matcher.matches();
    }
}
