package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.BoardMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Board;
import com.learnings.forum.services.IBoradService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-10-29
 * Time: 22:50
 */

@Service
@Slf4j
public class BoardServiceImpl implements IBoradService {


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
}
