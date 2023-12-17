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
 * Description:实现ArticleService
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
        return articleMapper.selectAll();
    }

    @Override
    public List<Article> selectAllWithPsize(Integer psize, Integer offset) {
        // 调用dao
        return articleMapper.selectAllWithPsize(psize, offset);
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
        return articleMapper.selectAllByBoardId(boardId);
    }

    @Override
    public List<Article> selectAllByBoardIdWithPsize(Long boardId, Integer psize, Integer offset) {
        //1-非空校验
        if(boardId == null || boardId <= 0
                || psize == null
                || offset == null) {
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
        return articleMapper.selectAllByBoardIdWithPsize(boardId,psize,offset);
    }

    @Override
    public List<Article> selectByUserId(Long userId) {
        //1-非空校验
        if(userId == null || userId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-校验用户是否存在
        User user = userService.selectById(userId);
        if(user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString() + ", boardId = " + userId);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        //3-调用dao
        List<Article> articles = articleMapper.selectByUserId(userId);
        //4-返回结果
        return articles;
    }

    @Override
    public Article selectDetailById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-调用DAO，获得帖子详情
        Article article = articleMapper.selectDetailById(id);
        //3-校验查询结果
        if(article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //4-更新帖子的访问次数
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitCount(article.getVisitCount()+1);
        //5-保存到数据库
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        //6-更新返回对象的访问次数
        article.setVisitCount(article.getVisitCount()+1);
        //6-返回帖子详情
        return article;
    }



    @Override
    public void modify(Long id, String title, String content) {
        //1-非空校验
        if(id == null || id <= 0
                || StringUtil.isEmpty(title) || StringUtil.isEmpty(content)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-构建要更新的帖子对象
        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setTitle(title);
        updateArticle.setContent(content);
        updateArticle.setUpdateTime(new Date());

        //3-调用dao
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }
    @Override
    public Article selectById(Long id) {
        if(id == null || id <= 0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        Article article = articleMapper.selectByPrimaryKey(id);

        return article;
    }

    @Override
    public void thumbsUpById(Long id) {
        //1-非空校验
        if(id == null || id <= 0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-获取帖子详情
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1 ) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        if(article.getState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        //3-构造要更新的对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setLikeCount(article.getLikeCount()+1);
        updateArticle.setUpdateTime(new Date());

        //4-调用dao
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void deleteById(Long id) {
        //1-非空校验
        if(id == null || id <= 0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-根据id查询帖子信息
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString() + ", article id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //3-创建更新对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setDeleteState((byte) 1);
        //4-调用dao
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //5-更新板块中的帖子数量
        boardService.subOneArticleCountById(article.getBoardId());
        //6-更新用户发帖数
        userService.subOneArticleCountById(article.getUserId());
        log.info("删除帖子成功, article id = " + article.getId() + ", user id = " + article.getUserId());
    }

    @Override
    public void addOneReplyCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-获取帖子信息
        //帖子已删除
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //帖子已封帖
        if(article.getState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }

        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        //回复数 = 原回复数+1
        updateArticle.setReplyCount(article.getReplyCount()+1);
        updateArticle.setUpdateTime(new Date());
        //3-执行更新操作
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public List<Article> selectByKeyWord(String keyword) {
        if(keyword == null) {
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        return articleMapper.selectByKeyWord(keyword);
    }
}
