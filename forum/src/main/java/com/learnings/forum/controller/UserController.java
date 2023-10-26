package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.MD5Util;
import com.learnings.forum.utils.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description: 用户
 * User: 12569
 * Date: 2023-10-17
 * Time: 22:51
 */

//对controller进行api接口的说明
@Api(tags = "用户接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 用户注册
     * @param username 用户名
     * @param nickname 用户昵称
     * @param password 密码
     * @param passwordRepeat 确认密码
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NonNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NonNull String nickname,
                              @ApiParam("密码") @RequestParam("password") @NonNull String password,
                              @ApiParam("确认密码") @RequestParam("password_repeat") @NonNull String passwordRepeat){
        //1-校验密码与重复密码是否相同
        if(!password.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            //返回错误信息
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

        //2-准备数据
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        //处理密码
            //生成盐
        String salt = UUIDUtil.UUID_32();
            //和明文密码合并，生成密码密文
        String encryptPassword = MD5Util.md5Salt(password, salt);
        user.setPassword(encryptPassword);
        user.setSalt(salt);

        //3-调用service层处理数据
        userService.createNormalUser(user);

        //4-返回
        return AppResult.success();
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request,
                           @ApiParam("用户名") @RequestParam("username") @NonNull String username,
                           @ApiParam("密码") @RequestParam("password") @NonNull String password) {
        //1-调用service中登录方法，返回User对象
        User user = userService.login(username, password);
        if(user == null) {
            //打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString());
            //返回结果
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        //2-如果登录成功，将User对象设置到Session作用域中
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.USER_SESSION, user);
        //3-返回结果
        return AppResult.success();
    }


    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @ApiParam("用户ID") @RequestParam(value = "id", required = false) Long id){
        //定义返回的user对象
        User user = null;
        //根据入参id的值判断user对象的获取方式
        if(id == null) {
            //1-如果id为null，则从session中获取当前登录的用户信息
            HttpSession session = request.getSession(false);
            //2-判断session和用户信息是否有效
            if(session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
                //表示用户没有登录，返回错误信息
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            //从session中获取当前登录的用户信息
            user = (User) session.getAttribute(AppConfig.USER_SESSION);
        }else{
            //如果id不为null，则从数据库中查询出用户信息
            user = userService.selectById(id);
        }
        //判断user是否为null 给前端返回有效的数据
        if(user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        //正常返回
        return AppResult.success(user);
    }
}
