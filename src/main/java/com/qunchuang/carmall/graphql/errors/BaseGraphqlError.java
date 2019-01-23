package com.qunchuang.carmall.graphql.errors;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * 原版的异常处理不是很好则又对其重新扩展
 *
 * @author zzk
 * @date 2018/11/10
 */
public class BaseGraphqlError implements GraphQLError {

    private List<Object> path;

    private String message;

    private List<SourceLocation> locations;

    public BaseGraphqlError(GraphQLError error, String message) {
        this.path = error.getPath();
        this.message = message;
        this.locations = error.getLocations();
    }

    public BaseGraphqlError(GraphQLError error) {
        this(error, error.getMessage());
    }

    @Override
    public final String getMessage() {
        return message;
    }

    @Override
    public final List<SourceLocation> getLocations() {
        return locations;
    }


    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public final List<Object> getPath() {
        return path;
    }


}
