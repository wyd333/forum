package com.learnings.forum.utils;

/**
 * Created with IntelliJ IDEA.
 * Description: 字符串工具类
 * User: 12569
 * Date: 2023-10-06
 * Time: 21:36
 */
public class StringUtil {
    /**
     * 判断字符串是否为空
     * @param value 要判断的字符串
     * @return true 空 <br/> false 非空
     */
    public static boolean isEmpty(String value){
        return value == null || value.length() == 0;
    }
}
