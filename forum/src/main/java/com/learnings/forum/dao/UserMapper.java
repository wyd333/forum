package com.learnings.forum.dao;

import com.learnings.forum.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    User selectByUserName(@Param("username") String username);

    /**
     * 查询用户发帖总数
     * @param id 用户id
     * @return 用户发帖总数
     */
    Integer selectArticleCountById(@Param("id") Long id);

    /**
     * 查询用户获赞总数
     * @param id 用户id
     * @return 用户获赞总数
     */
    Integer selectAllLikesCountById(@Param("id") Long id);




}