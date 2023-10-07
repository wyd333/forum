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
}
