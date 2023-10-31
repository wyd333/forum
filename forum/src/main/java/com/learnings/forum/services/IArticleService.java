package com.learnings.forum.services;

import com.learnings.forum.model.Article;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * Description: 帖子业务接口
 * User: 12569
 * Date: 2023-10-31
 * Time: 23:19
 */
public interface IArticleService {

    /**
     * 发布帖子
     * @param article 要发布的帖子
     */

    @Transactional  //当前方法中的执行过程会被事务管理起来
    void create(Article article);
}
