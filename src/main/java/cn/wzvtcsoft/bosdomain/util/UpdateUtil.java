package cn.wzvtcsoft.bosdomain.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * UpdateUtil
 *
 * @author zzk
 * @date 2018/11/14
 */
public class UpdateUtil {


    /**
     * 通过 properties 来更新对象。
     * 值得注意的是，若 properties 中的属性是初始化的属性，则不会进行更新
     *
     * @param src        要更新的源对象
     * @param target     更新的目标对象
     * @param properties 要更新的属性
     */
    public static <T> void updateProperties(T src, T target, String... properties) {

        String[] ignores = Arrays.stream(src.getClass().getDeclaredFields())
                .map(Field::getName)
                .filter(it -> {
                    for (String property : properties) {
                        if (it.equals(property)) {
                            return false;
                        }
                    }
                    return true;
                })
                .toArray(String[]::new);

        BeanWrapperImpl srcBean = new BeanWrapperImpl(src);
        for (int i = 0; i < ignores.length; i++) {
            String ignoreStr = ignores[i];
            Object propertyValue = srcBean.getPropertyValue(ignoreStr);
            if (propertyValue == null || propertyValue.equals(0) || propertyValue.equals(0.0) || "".equals(propertyValue)) {
                ignores[i] = "";
            }
        }

        BeanUtils.copyProperties(src, target, ignores);
    }
}


