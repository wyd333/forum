package com.learnings.forum.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleReply implements Serializable {
    private Long id;

    //帖子Id，关联article表
    private Long articleId;

    //回复者的用户Id
    private Long postUserId;

    //保留字段，支持楼中楼功能
    private Long replyId;

    //保留字段，支持楼中楼功能
    private Long replyUserId;

    //回复正文
    private String content;

    //点赞功能
    private Integer likeCount;

    //状态 0正常，1禁用
    private Byte state;

    //删除状态 0正常，1删除
    private Byte deleteState;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //关联对象-回复的发送者
    private User user;

}