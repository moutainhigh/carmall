package com.qunchuang.carmall.graphql.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * 为了让原本的Controller中的requestMapping能方便的切换到GraphQL mutation，互相切换
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphqlMutation {
    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};
}
