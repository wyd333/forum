package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Board;
import com.learnings.forum.services.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 板块
 * User: 12569
 * Date: 2023-10-29
 * Time: 23:04
 */

@Api(tags = "板块接口")
@RestController
@Slf4j
@RequestMapping("/board")
public class BoardController {

    //从配置文件中读取值，若未配置则默认值是9
    @Value("${fox-forum.index.board-num:9}")
    private Integer indexBoardNum;

    @Resource
    private IBoardService boradService;

    /**
     * 查询首页板块列表
     * @return
     */
    @ApiOperation("获取首页板块列表")
    @GetMapping("/top_list")
    public AppResult<List<Board>> topList(){
        log.info("首页板块个数为：" + indexBoardNum);
        List<Board> boards = boradService.selectByNum(indexBoardNum);
        //判断是否为空
        if(boards == null) {
            boards = new ArrayList<>();
        }
        return AppResult.success(boards);
    }


    @ApiOperation("获取板块信息")
    @GetMapping("get_by_id")
    public AppResult<Board> getById(@ApiParam("板块id") @RequestParam("id") @NonNull Long id){
        //1-调用service
        Board board = boradService.selectById(id);
        //2-对查询结果进行校验
        if(board == null || board.getDeleteState() == 1) {
            //打印日志
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //3-返回结果
        return AppResult.success(board);
    }

}
