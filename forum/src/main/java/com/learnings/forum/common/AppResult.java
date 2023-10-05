package com.learnings.forum.common;

/**
 * Created with IntelliJ IDEA.
 * Description: 定义返回结果。
 * 系统实现前后端分离，统一返回JSON格式的字符串，需要定义一个类，其中包含状态码，描述信息，返回的结果数据
 * User: 12569
 * Date: 2023-10-05
 * Time: 23:38
 */
public class AppResult<T> {
    //状态码
    private int code;
    //描述信息
    private String message;
    //具体数据
    private T data;

    /**
     * 构造方法
     * @param code
     * @param message
     */

    public AppResult(int code, String message) {
        this(code,message,null);
    }
    public AppResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功
     * @return
     */
    public static AppResult success(){
        return new AppResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功-自定义返回信息message
     * @param message
     * @return
     */
    public static AppResult success(String message){
        return new AppResult(ResultCode.SUCCESS.getCode(),message);
    }

    /**
     * 成功-传入具体数据data
     * @param data
     * @return
     * @param <T>
     */
    public static <T> AppResult<T> success(T data){
        return new AppResult<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    /**
     * 成功-传入具体数据data且自定义返回信息message
     * @param message
     * @param data
     * @return
     * @param <T>
     */
    public static <T> AppResult<T> success(String message, T data){
        return new AppResult<>(ResultCode.SUCCESS.getCode(),message,data);
    }

    /**
     * 失败
     * @return
     */
    public static AppResult failed(){
        return new AppResult(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMessage());
    }

    /**
     * 失败-自定义返回信息message
     * @param message
     * @return
     */
    public static AppResult failed(String message){
        return new AppResult(ResultCode.FAILED.getCode(),message);
    }

    /**
     * 失败-传入封装的状态码resultCode
     * @param resultCode
     * @return
     */
    public static AppResult failed(ResultCode resultCode){
        return new AppResult(resultCode.getCode(),resultCode.getMessage());
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
