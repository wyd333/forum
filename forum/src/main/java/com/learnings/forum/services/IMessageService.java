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

    /**
     * 根据用户Id查询当前用户的未读私信数
     * @param receiveUserId 用户Id
     * @return 未读数量
     */
    Integer selectUnreadCount(Long receiveUserId);
}
