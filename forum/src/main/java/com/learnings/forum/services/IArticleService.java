package com.learnings.forum.services;

import com.learnings.forum.model.Article;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    List<Article> selectAll();

    /**
     * 根据板块id查询帖子列表
     * @param boardId 板块id
     * @return
     */
    List<Article> selectAllByBoardId(Long boardId);

    /**
     * 根据帖子id查询详情
     * @param id 帖子id
     * @return 帖子详情
     */
    Article selectDetailById(Long id);

    /**
     * 编辑帖子
     * @param id 帖子id
     * @param title 帖子标题
     * @param content 帖子正文
     */
    public void modify(Long id, String title, String content);

    /**
     * 根据帖子id查询帖子
     * @param id 帖子id
     * @return
     */
    public Article selectById(Long id);

}
