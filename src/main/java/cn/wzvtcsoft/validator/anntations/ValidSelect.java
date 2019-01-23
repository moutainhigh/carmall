package cn.wzvtcsoft.validator.anntations;

import java.lang.annotation.*;

/**
 * 针对方法参数的选择校验规则。
 *
 * 使用逻辑组合来选择校验指定的方法参数的校验规则
 *
 * 规则的描述示例:
 *  @ValidSelect("person && age")
 *  public String demo5(@Valid @RequestBody Person person, @Min(18) int age, String username) {
 *         return "well";
 *  }
 *
 *  上述示例的作用是使 person 参数与 age 参数的校验都成功才算校验成功。
 *
 * @author zzk
 * @date 2018/10/2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidSelect {

    String value();

    String message() default "";

}