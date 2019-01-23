package com.qunchuang.carmall.graphql.errors;

import cn.wzvtcsoft.validator.errors.ParamInfo;
import cn.wzvtcsoft.validator.errors.ValidSelectError;
import graphql.ErrorType;
import graphql.GraphQLError;

import java.util.List;

/**
 * MutationValidError
 * 参数校验失败时的错误
 *
 * @author zzk
 * @date 2018/10/26
 */

public final class MutationValidError extends BaseGraphqlError {

    private List<ParamInfo> paramErrors;

    private int code = 400;

    public MutationValidError(GraphQLError error, ValidSelectError validSelectError) {
        super(error);
        this.paramErrors = validSelectError.getParamInfoList();
    }

    public List<ParamInfo> getParamErrors() {
        return paramErrors;
    }

    public int getCode() {
        return code;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.ValidationError;
    }
}
