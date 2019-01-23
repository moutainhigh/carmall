package com.qunchuang.carmall.graphql.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

/**
 * MethodParameterUtil
 *
 * @author zzk
 * @date 2018/09/28
 */
public class MethodParameterUtil {

    private static final ParameterNameDiscoverer discoverer =
            new DefaultParameterNameDiscoverer();


    /**
     * 根据指定的方法返回方法的参数名。
     * 数组的下标表示该参数所在的位置，数组中存储的字符串内容则表示参数名。
     *
     * @param method 指定的方法
     * @return 返回方法的参数名，若不能返回则为 Optional.empty()
     */
    public static Optional<String[]> getParamNames(Method method) {
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null) {
            return Optional.of(parameterNames);
        }
        return Optional.empty();
    }


    /**
     * 判断集合类是否存在泛型，若存在泛型则将集合类的类型转换成泛型类型
     *
     * @return 若是泛型则返回泛型的类型，不是则原样返回
     */
    public static Class convertGenericType(Parameter parameter) {
        Class typeClazz = parameter.getType();
        if (Collection.class.isAssignableFrom(typeClazz)) {
            Type genericSuperclass = parameter.getParameterizedType();
            return ResolvableType.forType(genericSuperclass).resolveGeneric(0);
        }
        return typeClazz;

    }





}
