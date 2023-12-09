package com.learnings.forum.services;

import com.learnings.forum.model.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 根据id查询站内信
     * @param id 站内信id
     * @return 站内信对象
     */
    Message selectById(Long id);

    /**
     * 根据用户Id查询当前用户的未读私信数
     * @param receiveUserId 用户Id
     * @return 未读数量
     */
    Integer selectUnreadCount(Long receiveUserId);

    /**
     *  根据接收者id查询私信列表
     * @param receiveUserId 接收者用户id
     * @return 消息列表
     */
    List<Message> selectByReceiveUserId(Long receiveUserId);

    /**
     * 更新指定站内信的状态
     * @param id 站内信id
     * @param state 目标状态
     */
    void updateStateById(Long id, Byte state);

    /**
     * 回复站内信
     * @param repliedId 要回复的站内信Id
     * @param message 回复的对象
     */
    @Transactional
    void reply(Long repliedId, Message message);
}
