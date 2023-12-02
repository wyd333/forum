package com.learnings.forum.interceptor;

import com.learnings.forum.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 登录拦截器
 * User: 12569
 * Date: 2023-10-28
 * Time: 21:16
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${fox-forum.login.url}")
    private String defaultURL;
    // 添加允许访问的页面
    private List<String> allowedURLs = Arrays.asList("/return.html", "/mailBack.html");
    /**
     * 前置处理：对请求的预处理
     * @return true ：继续流程 <br/> false : 流程中断
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的URI
        String requestURI = request.getRequestURI();

        // 如果请求的是允许访问的页面，则直接放行
        if (allowedURLs.contains(requestURI)) {
            return true;
        }

        //获取session对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
            //用户为已登录状态，校验通过
            return true;
        }
        //校验URL是否正确
        if(!defaultURL.startsWith("/")) {
            defaultURL = "/" + defaultURL;
        }
        //校验不通过，跳转到登录界面
        response.sendRedirect(defaultURL);
        //中断流程
        return false;
    }
}
