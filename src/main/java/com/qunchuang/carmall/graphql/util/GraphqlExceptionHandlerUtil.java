package com.qunchuang.carmall.graphql.util;

import com.qunchuang.carmall.graphql.annotation.GExceptionHandler;
import com.qunchuang.carmall.graphql.errors.GraphqlExceptionHandler;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * GraphqlExceptionHandlerUtil
 *
 * @author zzk
 * @date 2018/11/11
 */
@Component
public class GraphqlExceptionHandlerUtil implements ApplicationListener<ApplicationStartedEvent> {

    private static final Map<String, GraphqlExceptionHandler> cache = new HashMap<>();


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Collection<Object> handlers = event.getApplicationContext().getBeansWithAnnotation(GExceptionHandler.class).values();
        for (Object handler : handlers) {
            Class<? extends Throwable> exception = handler.getClass().getAnnotation(GExceptionHandler.class).value();
            cache.put(exception.getCanonicalName(), (GraphqlExceptionHandler) handler);
        }
    }



    public static GraphqlExceptionHandler getExceptionHandler(Throwable e) {
        return cache.get(e.getClass().getCanonicalName());
    }
}
