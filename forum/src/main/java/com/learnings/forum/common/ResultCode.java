package com.learnings.forum.common;

/**
 * Created with IntelliJ IDEA.
 * Description: 状态码
 * User: 12569
 * Date: 2023-10-05
 * Time: 23:07
 */
public enum ResultCode {
//    定义状态码
    SUCCESS                         (0, "操作成功"),
    FAILED                          (1000, "操作失败"),
    FAILED_UNAUTHORIZED             (1001, "未授权"),
    FAILED_PARAMS_VALIDATE          (1002, "参数校验失败"),
    FAILED_FORBIDDEN                (1003, "禁止访问"),
    FAILED_CREATE                   (1004, "新增失败"),
    FAILED_NOT_EXISTS               (1005, "资源不存在"),

    //      关于用户
    AILED_USER_EXISTS               (1101, "用户已存在"),
    FAILED_USER_NOT_EXISTS          (1102, "用户不存在"),
    FAILED_LOGIN                    (1103, "用户名或密码错误"),
    FAILED_USER_BANNED              (1104, "您已被禁言, 请联系管理员, 并重新登录."),
    FAILED_TWO_PWD_NOT_SAME         (1105, "两次输入的密码不一致"),
    FAILED_USER_ARTICLE_COUNT      (1106,"更新帖子数量失败"),
    FAILED_USER_EXISTS      (1107,"用户已存在"),
    FAILED_CODE_ERROR      (1108,"验证码异常"),
    FAILED_EMAIL_ERROR      (1109,"邮箱错误"),
    FAILED_EMAILCODE      (1109,"验证码校验失败"),
    FAILED_EMAILCODE_EXPIRED      (1110,"验证码已过期"),


    //      板块相关
    FAILED_BOARD_ARTICLE_COUNT      (1201,"更新帖子数量失败"),
    FAILED_BOARD_BANNED             (1202,"板块状态异常"),
    FAILED_BOARD_NOT_EXISTS         (1203,"板块不存在"),

    //    帖子相关
    FAILED_ARTICLE_NOT_EXISTS          (1301, "帖子不存在"),
    FAILED_ARTICLE_BANNED          (1302, "作者已结帖，不可回复！"),

    //    站内信
    FAILED_MESSAGE_NOT_EXISTS          (1401, "站内信不存在"),
    FAILED_MESSAGE_TO_MYSELF          (1402, "不能回复自己的站内信!"),

    //    服务器相关
    ERROR_SERVICES                  (2000, "服务器内部错误"),
    ERROR_IS_NULL                   (2001, "IS NULL."),

    //      图片相关
    ERROR_PHOTO                     (3000, "图片错误");



    //状态码 枚举的默认是private
    int code;
    //描述信息
    String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "code = " + code + ", message = " + message + '.';
    }
}
