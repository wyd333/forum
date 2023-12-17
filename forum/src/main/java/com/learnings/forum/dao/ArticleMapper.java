package com.learnings.forum.dao;

import com.learnings.forum.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

    /**
     * 查询所有帖子列表
     * @return
     */
    List<Article> selectAll();

    /**
     * 分页查询版-查询所有帖子列表
     * @return
     */
    List<Article> selectAllWithPsize(@Param("psize") Integer psize, @Param("offset") Integer offset);

    /**
     * 根据板块id查询帖子列表
     * @param boardId 板块id
     * @return 该板块下的帖子列表
     */
    List<Article> selectAllByBoardId(@Param("boardId") Long boardId);

    /**
     * 分页查询版-根据板块id查询帖子列表
     * @param boardId 版块id
     * @param psize 每页帖子数
     * @param offset 偏移量
     * @return 该板块下的帖子列表
     */
    List<Article> selectAllByBoardIdWithPsize(@Param("boardId") Long boardId,
                                              @Param("psize") Integer psize,
                                              @Param("offset") Integer offset);

    /**
     * 根据帖子id查询详情
     * @param id 帖子id
     * @return 帖子详情
     */
    Article selectDetailById(@Param("id") Long id);

    /**
     * 根据用户id查询帖子列表
     * @param userId 用户id
     * @return 帖子列表
     */
    List<Article> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据关键词模糊查询
     * @param keyword 关键词
     * @return 帖子列表
     */
    List<Article> selectByKeyWord(@Param("keyword")String keyword);

}