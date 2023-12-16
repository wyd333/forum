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

    /**
     * 用户发帖数+1
     * @param id 用户id
     */
    void addOneArticleCountById(Long id);

    /**
     * 用户发帖数-1
     * @param id 用户id
     */
    void subOneArticleCountById(Long id);

    /**
     * 修改个人信息
     * @param user 要更新的对象
     */
    void modifyInfo(User user);

    /**
     * 修改密码
     * @param id 用户id
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    void modifyPassword(Long id, String newPassword, String oldPassword);

    /**
     * 重置密码
     * @param id 用户id
     * @param newPassword 新密码
     */
    void resetPwd(Long id, String newPassword);

    /**
     * 修改头像
     * @param id 用户id
     * @param relativePathUrl 头像的相对路径
     */
    void modifyAvatar(Long id, String relativePathUrl);

}
