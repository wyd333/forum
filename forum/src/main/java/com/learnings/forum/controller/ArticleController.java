package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Article;
import com.learnings.forum.model.Board;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IArticleService;
import com.learnings.forum.services.IBoardService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 文章
 * User: 12569
 * Date: 2023-11-03
 * Time: 22:48
 */

@Api(tags = "文章接口")
@Slf4j
@RequestMapping("/article")
@RestController
public class ArticleController {

    @Resource
    private IArticleService articleService;
    @Resource
    private IBoardService boardService;
    /**
     * 发布新帖
     * @param boardId 板块id
     * @param title 标题
     * @param content 文章内容
     * @return
     */

    @ApiOperation("发布新帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块ID") @RequestParam("boardId") @NonNull Long boardId,
                            @ApiParam("标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("文章内容") @RequestParam("content") @NonNull String content){
        //1-校验用户是否禁言
        HttpSession session = request.getSession(false);
        User user  = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1) {
            //用户已禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        //2-板块校验
        Board board = boardService.selectById(boardId.longValue());
        if(board == null || board.getDeleteState() == 1 || board.getState() == 1) {
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_CREATE);
        }

        //3-封装文章对象
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setBoardId(boardId);
        article.setUserId(user.getId());

        //4-调用Service
        articleService.create(article);

        //5-响应成果
        return AppResult.success();
    }

    /**
     * 获取帖子列表，如果传入了boardId，则按boardId号查找，如果没有传入则查询全部
     * @param boardId
     * @return
     */
    @ApiOperation("获取帖子列表")
    @GetMapping("/get_list_by_board_id")
    public AppResult<List<Article>>  getListByBoardId(@ApiParam("板块id") @RequestParam(value = "boardId", required = false) Long boardId){
        //返回的集合
        List<Article> articles;
        if(boardId == null) {
            //查询所有
            articles = articleService.selectAll();
        } else {
            articles = articleService.selectAllByBoardId(boardId);
        }
        //如果结果集为空，创建一个空集合
        if(articles == null) {
            articles = new ArrayList<>();
        }

        //响应结果
        return AppResult.success(articles);
    }

    @ApiOperation("根据帖子Id获取详情")
    @GetMapping("/details")
    public AppResult<Article> getDetails(HttpServletRequest request,
            @ApiParam("帖子Id") @RequestParam("id") @NotNull Long id){
        //**从session中获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);


        //1-调用service获取帖子详情
        Article article = articleService.selectDetailById(id);
        //2-判断结果是否为空
        if(article == null) {
            return AppResult.failed(ResultCode.FAILED_NOT_EXISTS);
        }

        //**判断当前用户是否为作者
        if(user.getId().equals(article.getUserId())) {
            // 标识为作者
            article.setOwn(true);
        }
        //3-返回结果
        return AppResult.success(article);
    }

    @ApiOperation("编辑文章内容")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest servletRequest,
                            @ApiParam("文章Id") @RequestParam("id") @NonNull Long id,
                            @ApiParam("文章标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("文章内容") @RequestParam("content") @NonNull String content){
        //1-从session中获取当前用户
        HttpSession session = servletRequest.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //判断用户是否被禁言
        if(user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //2-查询文章作者
        Article article = articleService.selectById(id);
        if(article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }

        if(!user.getId().equals(article.getUserId())) {
            log.warn(ResultCode.FAILED_FORBIDDEN.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_FORBIDDEN));
        }

        //判断帖子状态--已归档or已删除
        if(article.getState() == 1 || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_BANNED));
        }

        //3-调用service层
        articleService.modify(id, title, content);
        //4-打印成功日志
        log.info("更新帖子成功：Article Id = " + id + ", User Id = " + user.getId());
        return AppResult.success();
    }

    @PostMapping("/thumbs_up")
    public AppResult thumbsUp(HttpServletRequest request,
                                @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id) {
        //1-校验用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //2-调用service
        articleService.thumbsUpById(id);
        //3-返回结果
        return AppResult.success();
    }
}
