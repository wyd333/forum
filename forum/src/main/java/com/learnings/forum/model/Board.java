package com.learnings.forum.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Board implements Serializable {
    private Long id;

    private String name;

    private Integer articleCount;

    private Integer sort;

    private Byte state;

    private Byte deleteState;

    private Date createTime;

    private Date updateTime;

}