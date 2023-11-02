package com.learnings.forum.services.impl;

import com.learnings.forum.model.User;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.MD5Util;
import com.learnings.forum.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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

    @Test
    void selectByUserName() {
        User user = userService.selectByUserName("bitbo3434y");
        System.out.println(user);
    }

    @Test
    void login() {
        User user = userService.login("test444", "111111");
        System.out.println(user);
    }

    @Test
    void selectById() {
        User user = userService.selectById(0L);
        System.out.println(user);
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        userService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }
}