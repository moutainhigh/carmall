package com.qunchuang.carmall.graphql.errors;

import graphql.GraphQLError;

/**
 * ExceptionHandlerError
 *
 * @author zzk
 * @date 2018/11/21
 */
public class ExceptionHandlerError extends BaseGraphqlError {

    private Object body;

    public ExceptionHandlerError(GraphQLError error, Object body) {
        super(error);
        this.body = body;
    }

    public Object getBody() {
        return body;
    }
}
