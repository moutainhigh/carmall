package com.qunchuang.carmall.graphql.errors;

import graphql.GraphQLError;

/**
 * 目前 graphql-jpa 不成熟，可能需要对服务器内部的异常进行打印
 *
 * @author zzk
 * @date 2018/11/13
 */
public class ThrowableGraphqlError extends BaseGraphqlError {

    private int status = 500;

    private Throwable throwable;

    public ThrowableGraphqlError(GraphQLError error, Throwable throwable) {
        super(error, throwable.getMessage());
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getStatus() {
        return status;
    }
}
