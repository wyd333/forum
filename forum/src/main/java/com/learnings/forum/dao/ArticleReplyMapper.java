package com.learnings.forum.dao;

import com.learnings.forum.model.ArticleReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleReplyMapper {
    int insert(ArticleReply row);

    int insertSelective(ArticleReply row);

    ArticleReply selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ArticleReply row);

    int updateByPrimaryKey(ArticleReply row);

    /**
     * 根据帖子id查询所有回复
     * @param articleId
     * @return
     */
    List<ArticleReply> selectByArticleId(@Param("articleId") Long articleId);
}