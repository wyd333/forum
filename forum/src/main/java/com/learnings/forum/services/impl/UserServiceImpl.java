package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.UserMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.MD5Util;
import com.learnings.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: 用户业务接口
 * User: 12569
 * Date: 2023-10-07
 * Time: 20:38
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 注册-普通用户
     * @param user 用户信息
     */
    @Override
    public void createNormalUser(User user) {
        //1-非空校验
        if(user == null || StringUtil.isEmpty(user.getUsername())
                || StringUtil.isEmpty(user.getNickname())
                || StringUtil.isEmpty(user.getPassword())
                || StringUtil.isEmpty(user.getSalt())) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常，统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-按用户名查询用户信息
        User existsUser = userMapper.selectByUserName(user.getUsername());
        //2.1-判断用户是否已经存在
        if(existsUser != null) {
            log.warn(ResultCode.AILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.AILED_USER_EXISTS));
        }

        //3-新增用户流程，设置default值
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        //当前日期
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        // 4-写入数据库
        // 动态插入insertSelective
        int row = userMapper.insertSelective(user);
        if(row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_CREATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //打印日志
        log.info("新增用户成功! username = " + user.getUsername() + ".");
    }

    @Override
    public User selectByUserName(String username) {
        //非空校验
        if(StringUtil.isEmpty(username)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //返回查询的结果
        return userMapper.selectByUserName(username);
    }

    @Override
    public User login(String username, String password) {
        //1-非空校验
        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //2-按用户名查询用户信息
        User user = selectByUserName(username);
        //3-对查询结果作非空校验
        if(user == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString() + ", username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //4-对密码做校验
        String encryptPassword = MD5Util.md5Salt(password, user.getSalt());
        //5-用密文和数据库中存的用户密码进行比较
        if(!encryptPassword.equalsIgnoreCase(user.getPassword())) {
            // 打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString() + " | 密码错误！username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //6-登录成功，打印日志，返回用户信息
        log.info("登录成功！username = " + username);
        return user;
    }

    @Override
    public User selectById(Long id) {
        //1-非空校验
        if(id == null) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //2-调用dao查询数据库 并获取对象
        User user = userMapper.selectByPrimaryKey(id);
        //3-返回结果
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {

    }
}
