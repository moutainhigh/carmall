package cn.wzvtcsoft.bosdomain.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EntityEnum
 *
 * @author zzk
 * @date 2018/10/17
 */


@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityEnum {

    /**
     * 存储在数据库中的值
     */
    String value() default "";

    /**
     * 对外的描述
     * @return
     */
    String alias() default "";
}
