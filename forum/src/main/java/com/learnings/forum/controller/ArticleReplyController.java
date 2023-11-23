package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Article;
import com.learnings.forum.model.ArticleReply;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IArticleReplyService;
import com.learnings.forum.services.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 回复帖子
 * User: 12569
 * Date: 2023-11-21
 * Time: 21:47
 */


@Api(tags = "回复接口")
@Slf4j
@RestController
@RequestMapping("/reply")
public class ArticleReplyController {
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleReplyService articleReplyService;

    @ApiOperation("回复帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("帖子Id") @RequestParam("articleId") @NonNull Long articleId,
                            @ApiParam("帖子内容") @RequestParam("content") @NonNull String content){
        //1-获取用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //2-判断用户是否已经禁言
        if(user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BANNED));
        }
        //3-获取要回复的帖子对象
        Article article = articleService.selectById(articleId);
        if(article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        if(article.getState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }

        //4-构建回复对象
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(article.getId());
        articleReply.setPostUserId(user.getId());
        articleReply.setContent(content);

        //4-写入回复信息 调用service
        articleReplyService.create(articleReply);

        return AppResult.success();
    }

    @ApiOperation("获取回复列表")
    @GetMapping("/get_replies")
    public AppResult<List<ArticleReply>> getRepliesByArticleId(@ApiParam("帖子id") @RequestParam("articleId") @NonNull Long articleId){
        //1-校验帖子是否存在
        Article article = articleService.selectById(articleId);
        if(article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //2-调用service
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);
        //3-返回结果
        return AppResult.success(articleReplies);

    }
}
