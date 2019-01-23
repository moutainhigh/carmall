package com.qunchuang.carmall.graphql.errors;

/**
 * BusinessException
 * 业务逻辑异常，所有的业务逻辑的异常都应该继承此异常
 *
 * @author zzk
 * @date 2018/10/26
 */
public class BusinessException extends RuntimeException {


    private int status;

    public int getStatus() {
        return status;
    }

    public BusinessException(String message, int status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message) {
        this(message, 400);
    }


}
