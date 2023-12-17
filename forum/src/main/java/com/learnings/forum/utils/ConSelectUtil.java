package com.learnings.forum.utils;

import com.learnings.forum.model.Article;
import com.learnings.forum.services.IArticleService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created with IntelliJ IDEA.
 * Description: 并发查询板块下的帖子列表
 * User: 12569
 * Date: 2023-12-17
 * Time: 16:05
 */

@Component
public class ConSelectUtil {
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;    //并发编程

    @Resource
    private IArticleService articleService;


}
