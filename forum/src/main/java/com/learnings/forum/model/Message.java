package com.learnings.forum.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Message implements Serializable {
    private Long id;

    private Long postUserId;

    private Long receiveUserId;

    private String content;

    private Byte state;

    private Byte deleteState;

    private Date createTime;

    private Date updateTime;

    /**
     * 关联发送者对象
     */
    private User postUser;

}