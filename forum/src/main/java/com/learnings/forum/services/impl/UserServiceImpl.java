package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.UserMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-10-07
 * Time: 20:38
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;

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
        //2.1-判断用户是否存在
        if(existsUser != null) {
            log.warn(ResultCode.AILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.AILED_USER_EXISTS));
        }

        //3-新增用户流程，设置默认值
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        //当前日期
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        //4-写入数据库，动态插入
        int row = userMapper.insertSelective(user);
        if(row != 1) {
            // 打印日志
            log.warn(ResultCode.FAILED_CREATE.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //打印日志
        log.info("新增用户成功！username = " + user.getUsername() + ".");


    }
}