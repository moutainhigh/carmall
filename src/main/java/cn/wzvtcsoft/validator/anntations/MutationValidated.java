package cn.wzvtcsoft.validator.anntations;


import java.lang.annotation.*;

/**
 * MutationValidated
 * 若被该注解则会进行校验
 *
 * @author zzk
 * @date 2018/10/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MutationValidated {
}
