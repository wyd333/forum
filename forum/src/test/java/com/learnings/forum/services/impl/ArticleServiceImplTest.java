package com.learnings.forum.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.forum.model.Article;
import com.learnings.forum.services.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 帖子service测试类
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
    @Transactional
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

    @Test
    void selectAllByBoardId() throws JsonProcessingException {
        List<Article> articles = articleService.selectAllByBoardId(10L);
        System.out.println(objectMapper.writeValueAsString(articles));  //对象转成json字符串
    }

    @Test
    @Transactional
    void selectDetailById() throws JsonProcessingException {
        Article article = articleService.selectDetailById(1L);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    @Transactional
    void modify() {
        articleService.modify(1L,"单元测试111", "帖子内容111");
        System.out.println("更新成功！");
    }

    @Test
    void selectById() throws JsonProcessingException {
        Article article = articleService.selectById(1L);
        System.out.println(objectMapper.writeValueAsString(article));
    }

    @Test
    @Transactional
    void thumbsUpById() {
        articleService.thumbsUpById(1L);
        System.out.println("ok!");
    }

    @Test
    @Transactional
    void deleteById() {
        articleService.deleteById(7L);
        System.out.println("ok!");
    }

    @Test
    @Transactional
    void addOneReplyCountById() {
        articleService.addOneReplyCountById(1L);
        System.out.println("ok!");
    }

    @Test
    void selectByUserId() throws JsonProcessingException {
        List<Article> articles = articleService.selectByUserId(8L);
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectAllByBoardIdWithPsize() throws JsonProcessingException {
        List<Article> articles = articleService.selectAllByBoardIdWithPsize(1L, 1, 2);
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectAllWithPsize() throws JsonProcessingException {
        List<Article> articleList = articleService.selectAllWithPsize(2, 1);
        System.out.println(objectMapper.writeValueAsString(articleList));
    }

    @Test
    void selectByKeyWord() throws JsonProcessingException {
        List<Article> articleList = articleService.selectByKeyWord("测试");
        System.out.println(objectMapper.writeValueAsString(articleList));
    }
}