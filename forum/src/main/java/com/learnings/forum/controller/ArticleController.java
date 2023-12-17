package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Article;
import com.learnings.forum.model.Board;
import com.learnings.forum.model.User;
import com.learnings.forum.model.vo.ArticleVO;
import com.learnings.forum.services.IArticleService;
import com.learnings.forum.services.IBoardService;
import com.learnings.forum.services.IUserService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created with IntelliJ IDEA.
 * Description: 帖子
 * User: 12569
 * Date: 2023-11-03
 * Time: 22:48
 */

@Api(tags = "帖子接口")
@Slf4j
@RequestMapping("/article")
@RestController
public class ArticleController {

    @Resource
    private IArticleService articleService;
    @Resource
    private IBoardService boardService;
    @Resource
    private IUserService userService;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;    //并发编程
    /**
     * 发布新帖
     * @param boardId 板块id
     * @param title 标题
     * @param content 帖子内容
     * @return
     */

    @ApiOperation("发布新帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @ApiParam("板块ID") @RequestParam("boardId") @NonNull Long boardId,
                            @ApiParam("标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("帖子内容") @RequestParam("content") @NonNull String content){
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

        //3-封装帖子对象
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
     * 获取帖子列表
     * 如果传入了boardId，则按boardId号查找
     * 如果没有传入则查询全部
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

    /**
     * 分页查询版-获取帖子列表
     * 如果传入了boardId，则按boardId号查找
     * 如果没有传入则查询全部
     * @param boardId
     * @return
     */
    @ApiOperation("分页查询版-获取帖子列表")
    @GetMapping("/get_list_by_boardId_psize")
    public AppResult<HashMap<String, Object>> getListByBoardIdWithPsize(@ApiParam("板块id") @RequestParam(value = "boardId", required = false) Long boardId,
                                                              @ApiParam("每页帖子数") @RequestParam(value = "psize", required = false) Integer psize,
                                                              @ApiParam("当前页码") @RequestParam(value = "pindex", required = false) Integer pindex) throws ExecutionException, InterruptedException {
        // 1-定义返回的结果集合和总页数
        List<Article> articles;
        Integer size;
        // 2-参数校正
        if(psize == null || psize <= 0) {
            psize = 10;
        }
        if(pindex == null || pindex <= 0) {
            pindex = 1;     //默认首页
        }

        // 3-调用service
        Integer finalPSize = psize;
        int offset = psize *(pindex -1);    //分页公式

        // 没有传入boardId时
        if(boardId == null) {   //查询所有
            // *帖子列表分页查询
            FutureTask<List<Article>> articleTask = new FutureTask<>(() -> {
                return articleService.selectAllWithPsize(finalPSize, offset);
            });
            taskExecutor.submit(articleTask);

            // *帖子总页数查询
            FutureTask<Integer> pageTask = new FutureTask<>(() -> {
                // 帖子总数
                int totalCount = articleService.selectAll().size();
                return (int) Math.ceil(totalCount * 1.0 / finalPSize);
            });
            taskExecutor.submit(pageTask);

            // *获得结果
            articles = articleTask.get(); //分页查询出的帖子列表
            size = pageTask.get();  //帖子总页数
        } else {    //传入boardId时
            // *帖子列表分页查询
            FutureTask<List<Article>> articleTask = new FutureTask<>(() -> {
                return articleService.selectAllByBoardIdWithPsize(boardId, finalPSize, offset);
            });
            taskExecutor.submit(articleTask);

            // *帖子总页数查询
            FutureTask<Integer> pageTask = new FutureTask<>(() -> {
                // 帖子总数
                int totalCount = articleService.selectAllByBoardId(boardId).size();
                return (int) Math.ceil(totalCount * 1.0 / finalPSize);
            });
            taskExecutor.submit(pageTask);

            // *获取结果
            articles = articleTask.get(); //分页查询出的帖子列表
            size = pageTask.get();  //帖子总页数
        }

        //如果结果集为空，创建一个空集合
        if(articles == null) {
            articles = new ArrayList<>();
        }

        // 4-组装数据
        HashMap<String, Object> listAndSizeMap = new HashMap<>();
        listAndSizeMap.put("articles", articles);
        listAndSizeMap.put("size", size);

        //响应结果
        return AppResult.success(listAndSizeMap);
    }



    @ApiOperation("根据帖子Id获取详情")
    @GetMapping("/details")
    public AppResult<ArticleVO> getDetails(HttpServletRequest request,
                                           @ApiParam("帖子Id") @RequestParam("id") @NotNull Long id) throws ExecutionException, InterruptedException {
        //**从session中获取当前登录的用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        //1-调用service获取帖子详情
        Article article = articleService.selectDetailById(id);
        //2-判断结果是否为空
        if(article == null) {
            return AppResult.failed(ResultCode.FAILED_NOT_EXISTS);
        }

        //3-根据作者id查询帖子作者的获赞总数
        FutureTask<Integer> likeCountTask = new FutureTask<>(() -> {
            // todo: 调用service
            return userService.selectAllLikesCountById(article.getUserId());
        });
        taskExecutor.submit(likeCountTask);

        //4-根据user id查询帖子作者发表的总帖子数
        FutureTask<Integer> articleCountTask = new FutureTask<>(() -> {
            // todo: 调用service
            return userService.selectArticleCountById(article.getUserId());
        });
        taskExecutor.submit(articleCountTask);

        //**判断当前用户是否为作者
        if(user.getId().equals(article.getUserId())) {
            // 标识为作者
            article.setOwn(true);
        }

        //5-组装数据
        int likeCount = likeCountTask.get();   // 等待任务（线程池）执行完成
        int articleCount = articleCountTask.get();  // 等待任务（线程池）执行完成

        ArticleVO articleVO = new ArticleVO(likeCount, articleCount, article);


        //3-返回结果
        return AppResult.success(articleVO);
    }

    @ApiOperation("编辑帖子内容")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest servletRequest,
                            @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id,
                            @ApiParam("帖子标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("帖子内容") @RequestParam("content") @NonNull String content){
        //1-从session中获取当前用户
        HttpSession session = servletRequest.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //判断用户是否被禁言
        if(user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //2-查询帖子作者
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

    @ApiOperation("点赞")
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

    @ApiOperation("删除帖子")
    @PostMapping("/delete")
    public AppResult deleteById(HttpServletRequest request,
                                @ApiParam("帖子Id") @RequestParam("id") @NonNull Long id){
        //1-校验用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1) {
            //用户已被禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //2-查询帖子详情
        Article article = articleService.selectById(id);
        if(article == null || article.getDeleteState() == 1) {
            //帖子已删除
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        //3-校验当前用户是否是作者
        if(!user.getId().equals(article.getUserId())) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        //4-调用service
        articleService.deleteById(id);
        //5-返回成功信息
        return AppResult.success();
    }

    @ApiOperation("获取用户的帖子列表")
    @GetMapping("/get_all_by_userid")
    public AppResult<List<Article>> getAllByUserId(HttpServletRequest request,
                                    @ApiParam("用户id") @RequestParam(value = "userId",required = false) Long userId){
        if(userId == null) {
            //获取session
            HttpSession session = request.getSession(false);
            //获取user对象
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            userId = user.getId();
        }
        //调用service
        List<Article> articles = articleService.selectByUserId(userId);
        return AppResult.success(articles);
    }

    @ApiOperation("根据关键字获取用户的帖子列表")
    @GetMapping("/keyword")
    public AppResult<List<Article>> getByKeyword(@ApiParam("关键词") @RequestParam("keyword") @NonNull String keyword) {
        //1-调用service
        List<Article> articleList = articleService.selectByKeyWord(keyword);
        return AppResult.success(articleList);
    }
}
