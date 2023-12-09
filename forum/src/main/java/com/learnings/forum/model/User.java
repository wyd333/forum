package com.learnings.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("用户信息")
@Data
public class User implements Serializable {
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @JsonIgnore //不参与json序列化
    private String password;
    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("电话号码")
    private String phoneNum;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("性别")
    private Byte gender;

    @JsonIgnore //不参与json序列化
    private String salt;

    @ApiModelProperty("头像地址")
    @JsonInclude(JsonInclude.Include.ALWAYS)    //保证不论什么情况，该字段都要参与json序列化
    private String avatarUrl;

    @ApiModelProperty("发帖数量")
    private Integer articleCount;

    @ApiModelProperty("是否是管理员")
    private Byte isAdmin;

    @ApiModelProperty("个人简介")
    private String remark;

    @ApiModelProperty("用户状态")
    private Byte state;

    @JsonIgnore //不参与json序列化
    private Byte deleteState;

    @ApiModelProperty("注册日期")
    private Date createTime;

    private Date updateTime;

}