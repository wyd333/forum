package com.learnings.forum.services.impl;

import com.learnings.forum.model.Article;
import com.learnings.forum.services.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-11-02
 * Time: 22:51
 */

@SpringBootTest
class ArticleServiceImplTest {
    @Resource
    private IArticleService articleService;

    @Test
    void create() {
        Article article = new Article();
        article.setUserId(1L);
        article.setBoardId(1L);
        article.setTitle("单元测试");
        article.setContent("测试内容");
        articleService.create(article);
        System.out.println("发帖成功");
    }
}