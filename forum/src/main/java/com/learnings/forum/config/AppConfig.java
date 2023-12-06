package com.learnings.forum.config;

/**
 * Created with IntelliJ IDEA.
 * Description: 全局变量定义
 * User: 12569
 * Date: 2023-10-24
 * Time: 22:36
 */
public class AppConfig {
    /**
     * 用户session中的k值
     */
    public static final String USER_SESSION = "USER_SESSION";
    /**
     * 找回密码时临时存放用户名
     */
    public static final String RAW_USERNAME = "RAW_USERNAME";
    /**
     * 找回密码时临时存放验证码
     */
    public static final String RAW_EMAILCODE = "RAW_EMAILCODE";

    /**
     * 过期时间
     */
    public static final Integer EXPIRE_SECONDS = 5;
}
