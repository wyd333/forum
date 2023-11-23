package com.learnings.forum.services;

import com.learnings.forum.model.ArticleReply;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 帖子回复
 * User: 12569
 * Date: 2023-11-20
 * Time: 21:08
 */
public interface IArticleReplyService {

    /**
     * 新增帖子回复
     * @param articleReply
     */
    @Transactional
    void create(ArticleReply articleReply);

    /**
     * 根据帖子id查询所有回复
     * @param articleId
     * @return
     */
    List<ArticleReply> selectByArticleId(Long articleId);
}
