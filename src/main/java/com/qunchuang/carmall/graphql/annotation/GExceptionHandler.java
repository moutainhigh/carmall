package com.qunchuang.carmall.graphql.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * GExceptionHandler
 *
 * @author zzk
 * @date 2018/11/11
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface GExceptionHandler {


    Class<? extends Throwable> value();

}
