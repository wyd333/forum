package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.BoardMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Board;
import com.learnings.forum.services.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:实现BoardService
 * User: 12569
 * Date: 2023-10-29
 * Time: 22:50
 */

@Service
@Slf4j
public class BoardServiceImpl implements IBoardService {


    @Resource
    private BoardMapper boardMapper;

    @Override
    public List<Board> selectByNum(Integer num) {
        //1-非空校验
        if(num <= 0) {
            // 打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-调用DAO查询数据库中的数据
        List<Board> result = boardMapper.selectByNum(num);
        //3-返回结果
        return result;
    }

    @Override
    public Board selectById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-调用DAO查询数据
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }

        //2-查询对应的板块
        Board board = boardMapper.selectByPrimaryKey(id);
        if(board == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", board id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        //3-更新帖子数量
        Board updateBoard = new Board();    //重新创建一个用来更新的对象
        //4-设置要更新的属性，调用动态更新的方法
        updateBoard.setId(id);
        updateBoard.setArticleCount(board.getArticleCount()+1);

        //5-调用dao，执行更新
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        //6-判断受影响的行数
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        //1-非空校验
        if(id == null || id <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        //2-查询板块详情
        Board board = boardMapper.selectByPrimaryKey(id);
        if(board == null) {
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + ", board id = " + id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //3-构造更新对象
        Board updateBoard = new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount()-1);
        //4-判断-1之后是否小于0
        if (updateBoard.getArticleCount() < 0) {
            //如果小于0，就设置为0
            updateBoard.setArticleCount(0);
        }

        //5-调用dao
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
