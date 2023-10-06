package com.learnings.forum.utils;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description: 生成UUID的工具类
 * User: 12569
 * Date: 2023-10-06
 * Time: 21:28
 */
public class UUIDUtil {
    /**
     * 生成一个36位的UUID
     * @return
     */
    public static String UUID_36(){
        return UUID.randomUUID().toString();
    }

    /**
     * 生成一个32位的UUID
     * 数据库中salt是32位，删去四个'-'刚好32位
     * @return
     */
    public static String UUID_32(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
