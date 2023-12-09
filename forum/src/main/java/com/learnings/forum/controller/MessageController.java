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
}
