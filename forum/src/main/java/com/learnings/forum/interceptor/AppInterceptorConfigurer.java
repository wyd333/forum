package com.learnings.forum.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description: 全局登录拦截器
 * User: 12569
 * Date: 2023-10-28
 * Time: 21:35
 */

//配置类
@Configuration
public class AppInterceptorConfigurer implements WebMvcConfigurer {
    //注入自定义的登录拦截器
    @Resource
    private LoginInterceptor loginInterceptor;

    //设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录拦截器
        registry.addInterceptor(loginInterceptor) // 添加用户登录拦截器
        .addPathPatterns("/**") // 拦截所有请求
        .excludePathPatterns("/user/login") // 排除登录api接⼝
        .excludePathPatterns("/user/register") // 排除注册api接⼝
        .excludePathPatterns("/user/logout") // 排除退出api接⼝
        .excludePathPatterns("/user/mail") // 排除发送邮箱验证码接口
        .excludePathPatterns("/user/email_code") // 排除验证邮箱验证码接口
        .excludePathPatterns("/user/resetPwd") // 排除重置密码接口
        .excludePathPatterns("/swagger*/**") // 排除登录swagger下所有
        .excludePathPatterns("/v3*/**") // 排除登录v3下所有，与swagger相关
        .excludePathPatterns("/dist/**") // 排除所有静态⽂件
        .excludePathPatterns("/image/**")
        .excludePathPatterns("/**.ico")
        .excludePathPatterns("/js/**");
    }
}
