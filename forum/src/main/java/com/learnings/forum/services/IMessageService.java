package com.learnings.forum.services;

import com.learnings.forum.model.Message;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-12-08
 * Time: 19:39
 */
public interface IMessageService {
    /**
     * 发送私信
     * @param message 站内信对象
     */
    void create  (Message message);
}
