package com.learnings.forum.services;

import com.learnings.forum.model.Board;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-10-29
 * Time: 22:41
 */
public interface IBoardService {
    /**
     * 查询 num 条记录
     * @param num 要查询的条数
     * @return 结果集
     */
    List<Board> selectByNum(Integer num);

    /**
     * 根据版块id查询版块信息
     * @param id 版块id
     * @return
     */
    Board selectById(Long id);


    /**
     * 版块中的帖子数 + 1
     * @param id 版块id
     */
    void addOneArticleCountById(Long id);

    /**
     * 版块中帖子数-1
     * @param id 版块id
     */
    void subOneArticleCountById(Long id);
}
