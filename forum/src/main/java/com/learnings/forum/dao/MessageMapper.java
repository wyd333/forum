package com.learnings.forum.dao;

import com.learnings.forum.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    /**
     * 根据用户Id查询当前用户的未读私信数
     * @param receiveUserId 用户Id
     * @return 未读数量
     */
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);
}