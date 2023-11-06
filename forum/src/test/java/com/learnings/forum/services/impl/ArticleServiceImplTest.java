package com.learnings.forum.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.forum.model.Article;
import com.learnings.forum.services.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

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

    @Resource
    private ObjectMapper objectMapper;

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

    @Test
    void selectAll() throws JsonProcessingException {
        //调用service
        List<Article> articles = articleService.selectAll();
        System.out.println(objectMapper.writeValueAsString(articles));  //对象转成json字符串
    }
}