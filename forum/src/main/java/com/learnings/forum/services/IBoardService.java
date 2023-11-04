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
     * 根据板块id查询板块信息
     * @param id 板块id
     * @return
     */
    Board selectById(Long id);


    /**
     * 更新板块中的帖子数据
     * @param id 板块id
     */
    void addOneArticleCountById(Long id);

}