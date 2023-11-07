package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.ArticleMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Article;
import com.learnings.forum.model.Board;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IArticleService;
import com.learnings.forum.services.IBoardService;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-10-31
 * Time: 23:31
 */

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {
    @Resource
    private ArticleMapper articleMapper;

    //用户和板块操作
    @Resource
    private IBoardService boardService;
    @Resource
    private IUserService userService;


    @Override
    public void create(Article article) {
        //1-非空校验
        if(article == null || article.getUserId() == null || article.getBoardId() == null
            || StringUtil.isEmpty(article.getTitle())
            || StringUtil.isEmpty(article.getContent())) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-设置默认值
        article.setVisitCount(0);   //访问数
        article.setReplyCount(0);   //回复数
        article.setLikeCount(0);    //点赞数
        article.setState((byte) 0);
        article.setDeleteState((byte) 0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);

        //3-写入数据库
        int articleRow = articleMapper.insertSelective(article);
        if(articleRow <= 0) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //4-更新用户发帖数
        User user = userService.selectById(article.getUserId());    //获取用户信息
        if(user == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + ", 发帖失败, user id = " + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //5-更新用户发帖数
        userService.addOneArticleCountById(user.getId());

        //6-获取板块信息
        Board board = boardService.selectById(article.getBoardId());

        //7-是否在数据库中有对应的板块
        if(board == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + ", 发帖失败, board id = " + article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //8-更新板块中的贴子数
        boardService.addOneArticleCountById(board.getId());

        //9-打印日志
        log.info(ResultCode.SUCCESS.toString() + "user id = " + article.getUserId()
                + ", board id = " + article.getBoardId()
                + ", article id = " + article.getId() + "，发帖成功");
    }


    @Override
    public List<Article> selectAll() {
        // 调用dao
        List<Article> articles = articleMapper.selectAll();
        return articles;
    }

    @Override
    public List<Article> selectAllByBoardId(Long boardId) {
        //1-非空校验
        if(boardId == null || boardId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2-校验板块是否存在
        Board board = boardService.selectById(boardId);
        if(board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + ", boardId = " + boardId);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }

        //3-调用dao，查询
        List<Article> articles = articleMapper.selectAllByBoardId(boardId);

        return articles;
    }
}
