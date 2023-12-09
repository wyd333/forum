package com.learnings.forum.services.impl;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.dao.MessageMapper;
import com.learnings.forum.exception.ApplicationException;
import com.learnings.forum.model.Message;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IMessageService;
import com.learnings.forum.services.IUserService;
import com.learnings.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-12-08
 * Time: 19:40
 */

@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private IUserService userService;
    @Override
    public void create(Message message) {
        //1-非空校验
        if(message == null
                || message.getPostUserId() == null
                || message.getReceiveUserId() == null
                || StringUtil.isEmpty(message.getContent())) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-校验接收者是否存在
        User user = userService.selectById(message.getReceiveUserId());
        if(user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //3-设置默认值
        message.setState((byte) 0); //设置默认值为0：未读状态  1为已读，2为已回复
        message.setDeleteState((byte) 0);
        //4-设置创建与更新时间
        Date date = new Date();
        message.setCreateTime(date);
        message.setUpdateTime(date);

        //5-调用dao
        int row = messageMapper.insertSelective(message);
        if(row != 1) {
            log.warn(ResultCode.FAILED_CREATE + ", 受影响的行数 != 1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));

        }
    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        //1-非空校验
        if(receiveUserId == null || receiveUserId <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-直接调用dao
        Integer count = messageMapper.selectUnreadCount(receiveUserId);
        // 正常的查询不可能出现null
        if(count == null) {
            //打印日志
            log.warn(ResultCode.ERROR_SERVICES.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        return count;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {
        //1-非空校验
        if(receiveUserId == null || receiveUserId <= 0) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2-调用dao查询
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);

        return messages;
    }
}
