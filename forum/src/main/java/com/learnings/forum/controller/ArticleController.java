package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
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
    public AppResult<Article> getDetails(@ApiParam("帖子Id") @RequestParam("id") @NotNull Long id){
        //1-调用service获取帖子详情
        Article article = articleService.selectDetailById(id);
        //2-判断结果是否为空
        if(article == null) {
            return AppResult.failed(ResultCode.FAILED_NOT_EXISTS);
        }
        //3-返回结果
        return AppResult.success(article);
    }
}
