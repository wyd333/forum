package com.learnings.forum.services.impl;

import com.learnings.forum.model.User;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.MD5Util;
import com.learnings.forum.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description: UserService测试类
 * User: 12569
 * Date: 2023-10-07
 * Time: 21:13
 */

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private IUserService userService;

    /**
     * 测试-注册普通用户
     */
    @Test
    void createNormalUser() {
        //构造User对象
        User user = new User();
        user.setUsername("bitboy");
        user.setNickname("zhangsan");

        //定义一个原始的密码
        String password = "123456";
        //生成盐
        String salt = UUIDUtil.UUID_32();
        //生成密码的密文
        String cipherText = MD5Util.md5Salt(password, salt);
        //设置加密后的密码
        user.setPassword(cipherText);
        //设置盐
        user.setSalt(salt);

        //调用service层方法
        userService.createNormalUser(user);
        System.out.println(user);
    }
}