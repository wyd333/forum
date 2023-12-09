package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import com.learnings.forum.config.AppConfig;
import com.learnings.forum.model.Message;
import com.learnings.forum.model.User;
import com.learnings.forum.services.IMessageService;
import com.learnings.forum.services.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-12-08
 * Time: 20:50
 */

@Slf4j
@Api(tags = "私信接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private IMessageService messageService;
    @Resource
    private IUserService userService;

    @ApiOperation("发送私信")
    @PostMapping("/send")
    public AppResult send(HttpServletRequest request,
                          @ApiParam("接收者Id") @RequestParam("receiveUserId") @NonNull Long receiveUserId,
                          @ApiParam("内容") @RequestParam("content") @NonNull String content){
        //1-当前登录的状态 禁言则不可发送
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1) {
            //返回提示信息
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //2-不能给自己发站内信
        if(user.getId().equals(receiveUserId)) {
            return AppResult.failed("不能给自己发送私信！");
        }
        //3-校验接收者是否存在
        User recipient = userService.selectById(receiveUserId);
        if(recipient == null || recipient.getDeleteState() == 1) {
            //返回接收者状态异常
            return AppResult.failed("用户状态异常！");
        }
        Message message = new Message();
        message.setPostUserId(user.getId());    //发送者Id
        message.setReceiveUserId(receiveUserId);    //接收者Id
        message.setContent(content);    //私信内容

        //4-封装对象
        messageService.create(message);
        //5-调用service

        //6-返回
        return AppResult.success("发送成功！");
    }

    @ApiOperation("获取未读私信数量")
    @GetMapping("/get_unread_count")
    public AppResult<Integer> getUnreadCount(HttpServletRequest request){
        //1-获取当前登录的用户信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //2-调用service
        //当前登录用户id即接收者id
        Integer count = messageService.selectUnreadCount(user.getId());
        //3-返回
        return AppResult.success(count);
    }

    @ApiOperation("查询用户收到的私信列表")
    @GetMapping("/get_all")
    public AppResult<List<Message>> getAll(HttpServletRequest request) {
        //1-获取当前登录的账户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //2-调用service
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());

        //3-返回
        return AppResult.success(messages);
    }

    @ApiOperation("更新站内信为已读状态")
    @PostMapping("/mark_read")
    public AppResult markRead(HttpServletRequest request,
            @ApiParam("站内信Id") @RequestParam("id") @NonNull Long id){
        // 1-查询当前站内信状态
        Message message = messageService.selectById(id);
        if(message == null || message.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        // 2-校验站内信是不是发给自己的
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(!user.getId().equals(message.getReceiveUserId())) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        //3-调用service，把状态更新成已读
        messageService.updateStateById(id, (byte) 1);
        //4-返回结果
        return AppResult.success();
    }

    /**
     * 回复站内信
     * @param repliedId 要回复的站内信id
     * @param content 要回复的站内信id
     * @return AppResult
     */
    @ApiOperation("回复站内信")
    @PostMapping("/reply")
    public AppResult reply(HttpServletRequest request,
                           @ApiParam("要回复的站内信id") @RequestParam("repliedId") @NonNull Long repliedId,
                           @ApiParam("要回复的站内信id") @RequestParam("content") @NonNull String content){
        // 1-校验当前用户状态
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if(user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 2-校验要回复的站内信状态
        Message existsMessage = messageService.selectById(repliedId);
        if(existsMessage == null || existsMessage.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }

        // 3-校验不能给自己回复
        if(user.getId().equals(existsMessage.getPostUserId())) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_TO_MYSELF);
        }

        // 4-校验当前用户是否是要回复的站内信的接收者
        if(!user.getId().equals(existsMessage.getReceiveUserId())) {
            return AppResult.failed(ResultCode.FAILED);
        }
        // 3-构造对象 Controller = 校验&&构造
        Message message = new Message();
        message.setPostUserId(user.getId());    //发送者
        message.setReceiveUserId(existsMessage.getPostUserId()); //接收者
        message.setContent(content);    //内容

        // 4-调用service
        messageService.reply(repliedId, message);
        // 5-返回结果
        return AppResult.success();

    }
}
