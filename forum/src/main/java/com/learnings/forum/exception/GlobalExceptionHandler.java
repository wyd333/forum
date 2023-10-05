package com.learnings.forum.exception;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description: 统一的全局异常处理
 * User: 12569
 * Date: 2023-10-06
 * Time: 0:05
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ApplicationException.class)   //定义具体要处理哪种异常
    public AppResult applicationExceptionHandler(ApplicationException e) {
        //打印异常信息
        e.printStackTrace();    //上生产环境之前要删除
        //打印日志
        log.error(e.getMessage());
        if(e.getErrorResult() != null) {
            return e.getErrorResult();
        }
        //非空校验
        if(e.getMessage() == null || e.getMessage().equals("")){
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        //返回具体的异常信息
        return AppResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AppResult exceptionHandler(Exception e){
        //打印异常信息
        e.printStackTrace();
        //打印日志
        log.error(e.getMessage());
        //非空校验
        if(e.getMessage() == null || e.getMessage().equals("")){
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        //返回错误信息
        return AppResult.failed(e.getMessage());
    }
}
