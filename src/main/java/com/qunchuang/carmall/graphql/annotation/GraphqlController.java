package com.qunchuang.carmall.graphql.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 为了让原本的Controller中的RestController能方便的切换到GraphQL mutation，互相切换
 *
 * @author zzk
 * @date 2018/9/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface GraphqlController {

    /**
     * Mutation Method 的前缀名称
     * mutation method 在 schema 中的前缀名称，类似 Controller 中的 @RequestMapping 的概念
     *
     * @return mutation method 的前缀名称
     */
    String value() default "";

}

