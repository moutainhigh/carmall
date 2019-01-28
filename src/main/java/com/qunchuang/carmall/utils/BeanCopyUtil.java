package com.qunchuang.carmall.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类属性值copy
 *
 * @author Curtain
 * @date 2019/1/28 9:11
 */

public class BeanCopyUtil {

    /**
     *  忽略基础属性值
     */
    private final static Set<String> IGNORE_PROPERTIES = new HashSet<String>();

    static {
        IGNORE_PROPERTIES.add("id");
        IGNORE_PROPERTIES.add("number");
        IGNORE_PROPERTIES.add("createtime");
        IGNORE_PROPERTIES.add("updatetime");
        IGNORE_PROPERTIES.add("createactorid");
        IGNORE_PROPERTIES.add("updateactorid");
        IGNORE_PROPERTIES.add("disabled");
    }

    /**
     * 获取值为Null的属性
     * @param source
     * @return
     */
    private static Set<String> getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> nullProperties = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullProperties.add(pd.getName());
            }
        }
        return nullProperties;
    }

    /**
     * 过滤
     * @param source 过滤只为Null属性
     * @param customer 过滤自定义属性
     * @return
     */
    public static String[] filterProperty(Object source, Set<String> customer) {
        return integrationProperty(source, customer);
    }


    /**
     * 过滤
     * @param source
     * @return
     */
    public static String[] filterProperty(Object source) {
        return integrationProperty(source, null);
    }

    /**
     * 整合过滤属性
     * @param source
     * @param customer
     * @return
     */
    private static String[] integrationProperty(Object source, Set<String> customer) {
        Set<String> result = new HashSet<>();
        result.addAll(IGNORE_PROPERTIES);
        result.addAll(getNullPropertyNames(source));
        if (customer != null) {
            result.addAll(customer);
        }

        return result.toArray(new String[result.size()]);
    }

}
