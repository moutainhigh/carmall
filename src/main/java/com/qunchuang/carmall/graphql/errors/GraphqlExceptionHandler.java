package com.qunchuang.carmall.graphql.errors;

/**
 * GExceptionHandler
 *
 * @author zzk
 * @date 2018/11/10
 */
public interface GraphqlExceptionHandler<E extends Throwable> {

    /**
     * 异常的详情数据
     */
    <T> T body(E e);


    /**
     * 响应的状态码,默认是 500
     */
    default int status() {
        return 500;
    }
}
