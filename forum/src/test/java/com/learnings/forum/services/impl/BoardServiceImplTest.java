package com.learnings.forum.services.impl;

import com.learnings.forum.model.Board;
import com.learnings.forum.services.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: BoardServiceImpl测试类
 * User: 12569
 * Date: 2023-10-29
 * Time: 22:58
 */

@SpringBootTest
class BoardServiceImplTest {
    @Resource
    private IBoardService boradService;

    @Test
    void selectByNum() {
        List<Board> boards = boradService.selectByNum(3);
        System.out.println(boards);
    }

    @Test
    @Transactional  //回滚数据库操作
    void addOneArticleCountById() {
        boradService.addOneArticleCountById(1L);
        System.out.println("更新成功!");
    }
}