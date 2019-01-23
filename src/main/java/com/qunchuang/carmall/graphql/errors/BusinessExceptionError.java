package com.qunchuang.carmall.graphql.errors;

import graphql.GraphQLError;

/**
 * BusinessExceptionError
 *
 * @author zzk
 * @date 2018/10/26
 */
public final class BusinessExceptionError extends BaseGraphqlError {


    private int status;

    public BusinessExceptionError(GraphQLError error, int status, String message) {
        super(error, message);
        this.status = status;

    }

    public BusinessExceptionError(GraphQLError error, BusinessException e) {
        this(error, e.getStatus(), e.getMessage());
    }


    public int getStatus() {
        return status;
    }
}
