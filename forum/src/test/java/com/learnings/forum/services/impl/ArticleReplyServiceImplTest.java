package com.learnings.forum.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.forum.model.ArticleReply;
import com.learnings.forum.services.IArticleReplyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-11-21
 * Time: 20:54
 */

@SpringBootTest
class ArticleReplyServiceImplTest {
    @Resource
    private IArticleReplyService articleReplyService;
    @Resource
    private ObjectMapper objectMapper;
    @Test
    @Transactional
    void create() {
        //构造一个回复对象
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(1L);
        articleReply.setPostUserId(8L);
        articleReply.setContent("单元测试回复111");
        //调用service的create方法
        articleReplyService.create(articleReply);
        System.out.println("ok");
    }

    @Test
    void selectByArticleId() throws JsonProcessingException {
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(1L);
        System.out.println(objectMapper.writeValueAsString(articleReplies));
    }
}