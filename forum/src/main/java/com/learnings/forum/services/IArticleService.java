package com.learnings.forum.services;

import com.learnings.forum.model.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
     * 分页查询版-查询所有帖子列表
     * @return
     */
    List<Article> selectAllWithPsize(Integer psize, Integer offset);


    /**
     * 根据板块id查询帖子列表
     * @param boardId 板块id
     * @return
     */
    List<Article> selectAllByBoardId(Long boardId);

    /**
     * 分页查询版-根据板块id查询帖子列表
     * @param boardId 版块id
     * @param psize 每页帖子数
     * @param offset 偏移量
     * @return 该板块下的帖子列表
     */
    List<Article> selectAllByBoardIdWithPsize(Long boardId,Integer psize,Integer offset);

    /**
     * 根据用户id查询帖子列表
     * @param userId 用户id
     * @return 帖子列表
     */
    List<Article> selectByUserId(Long userId);

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
    void modify(Long id, String title, String content);

    /**
     * 根据帖子id查询帖子
     * @param id 帖子id
     * @return
     */
    Article selectById(Long id);

    /**
     * 点赞帖子
     * @param id 帖子id
     */
    void thumbsUpById (Long id);

    /**
     * 根据id删除帖子
     * @param id 帖子id
     */
    void deleteById (Long id);

    /**
     * 帖子的回复数 + 1
     * @param id 帖子id
     */
    void addOneReplyCountById(Long id);

}
