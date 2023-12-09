package com.learnings.forum.services.impl;

import com.learnings.forum.model.Message;
import com.learnings.forum.services.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-12-08
 * Time: 19:53
 */
@SpringBootTest
class MessageServiceImplTest {
    @Resource
    private IMessageService messageService;

    @Test
    @Transactional
    void create() {
        Message message = new Message();
        message.setPostUserId(9L);
        message.setReceiveUserId(100L);
        message.setContent("单元测试");
        messageService.create(message);
        System.out.println("ok!");
    }

    @Test
    void selectUnreadCount() {
        Integer count = messageService.selectUnreadCount(700L);
        System.out.println("未读数量：" + count);
    }
}