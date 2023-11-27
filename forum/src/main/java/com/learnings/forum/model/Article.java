package com.learnings.forum.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Article {
    private Long id;

    private Long boardId;

    private Long userId;

    private String title;

    private Integer visitCount;

    private Integer replyCount;

    private Integer likeCount;

    private Byte state;


    private Byte deleteState;

    private Date createTime;

    private Date updateTime;

    private String content;

    @ApiModelProperty("是否为作者")
    private Boolean own;

    //关联对象-作者
    private User user;

    //关联对象-版块
    private Board board;

}