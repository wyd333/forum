package com.learnings.forum.services;

/**
 * Created with IntelliJ IDEA.
 * Description: 控制session类
 * User: 12569
 * Date: 2023-12-06
 * Time: 20:59
 */
public interface ISessionService {
    /**
     * 设置邮箱验证码的过期时间
     * @param sessionId
     * @param rawEmailCode
     */
    void setRawEmailCodeExpiration(String sessionId, String rawEmailCode);
    void setRawUsernameExpiration(String sessionId, String rawUsername);
    String getRawEmailCode(String sessionId);
    String getRawUsername(String sessionId);
}
