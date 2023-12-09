package com.learnings.forum.services.impl;

import com.learnings.forum.config.AppConfig;
import com.learnings.forum.services.ISessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 12569
 * Date: 2023-12-06
 * Time: 21:07
 */

@Service
@Slf4j
public class SessionServiceImpl implements ISessionService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void setRawEmailCodeExpiration(String sessionId, String rawEmailCode) {
        // 设置rawEmailCode字段的值
        stringRedisTemplate.opsForHash().put("sessionId:" + sessionId, AppConfig.RAW_EMAILCODE, rawEmailCode);
        // 设置rawEmailCode字段的过期时间
        stringRedisTemplate.expire("sessionId:" + sessionId, AppConfig.EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void setRawUsernameExpiration(String sessionId, String rawUsername) {
        // 设置RawUsername字段的值
        stringRedisTemplate.opsForHash().put("sessionId:" + sessionId, AppConfig.RAW_USERNAME, rawUsername);
        // 设置RawUsername字段的过期时间
        stringRedisTemplate.expire("sessionId:" + sessionId, AppConfig.EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public String getRawEmailCode(String sessionId) {
        String rawEmailCode = (String) stringRedisTemplate.opsForHash().get("sessionId:" + sessionId, AppConfig.RAW_EMAILCODE);
        return rawEmailCode;
    }

    @Override
    public String getRawUsername(String sessionId) {
        String rawUsername = (String) stringRedisTemplate.opsForHash().get("sessionId:" + sessionId, AppConfig.RAW_USERNAME);
        return rawUsername;
    }
}
