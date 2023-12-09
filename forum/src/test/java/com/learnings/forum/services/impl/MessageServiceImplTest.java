package com.learnings.forum.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.forum.model.Message;
import com.learnings.forum.services.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

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
    @Resource
    private ObjectMapper objectMapper;

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

    @Test
    void selectByReceiveUserId() throws JsonProcessingException {
        List<Message> messages = messageService.selectByReceiveUserId(800L);
        System.out.println(objectMapper.writeValueAsString(messages));
    }

    @Test
    @Transactional
    void updateStateById() {
        messageService.updateStateById(8L,(byte) 2);
        System.out.println("ok!");
    }

    @Test
    void selectById() throws JsonProcessingException {
        Message message = messageService.selectById(4L);
        System.out.println(objectMapper.writeValueAsString(message));
    }

    @Test
    void reply() {
        Message message = new Message();
        message.setPostUserId(8L);
        message.setReceiveUserId(9L);
        message.setContent("测试回复");

        //调用service
        messageService.reply(2L, message);
        System.out.println("ok!");
    }
}