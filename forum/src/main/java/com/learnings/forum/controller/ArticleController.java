package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.model.Article;
import com.learnings.forum.model.Board;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IArticleService;
import com.learnings.forum.services.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
}
