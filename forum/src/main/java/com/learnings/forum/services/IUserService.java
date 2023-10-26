package com.learnings.forum.services;

import com.learnings.forum.model.User;

/**
 * Created with IntelliJ IDEA.
 * Description: 用户接口
 * User: 12569
 * Date: 2023-10-07
 * Time: 20:33
 */
public interface IUserService {
    /**
     * 创建一个普通用户
     * @param user 用户信息
     */
    void createNormalUser(User user);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return User对象
     */
    User selectByUserName(String username);

    /**
     * 处理用户登录
     * @param username 用户名
     * @param password 密码
     * @return User对象
     */
    User login(String username, String password);

    /**
     * 根据id查询用户信息
     * @param id 用户id
     * @return User对象
     */
    User selectById(Long id);
}
