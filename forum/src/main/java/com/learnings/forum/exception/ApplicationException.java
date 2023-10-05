package com.learnings.forum.exception;

import com.learnings.forum.common.AppResult;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义异常
 * User: 12569
 * Date: 2023-10-05
 * Time: 23:57
 */
public class ApplicationException extends RuntimeException {
    //在异常中持有一个错误信息
    protected AppResult errorResult;

    /**
     * 构造方法
     * @param errorResult
     */
    public ApplicationException(AppResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public AppResult getErrorResult() {
        return errorResult;
    }
}
