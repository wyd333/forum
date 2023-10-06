package com.learnings.forum.utils;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created with IntelliJ IDEA.
 * Description: 用于MD5加密的工具类
 * User: 12569
 * Date: 2023-10-06
 * Time: 21:16
 */
public class MD5Util {
    /**
     * 对字符串进行md5加密
     * @param str 明文
     * @return 密文
     */
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }


    /**
     * 给密码进行加密，加盐加密
     * @param str 密码明文
     * @param salt 盐值，扰动字符
     * @return 密码密文
     */
    public static String md5Salt(String str, String salt){
        return md5(md5(str) + salt);
    }
}
