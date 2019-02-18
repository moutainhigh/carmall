package com.qunchuang.carmall.graphql.util;

import cn.wzvtcsoft.validator.anntations.*;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;

import javax.validation.Constraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

/**
 * SchemaParseUtil
 *
 * @author zzk
 * @date 2018/10/30
 */
public class SchemaParseUtil {

    /**
     * 由前端解析的换行标识符
     */
    public static final String SEPARATOR = "$$";

    /**
     * 表示方法上的所有参数都需要校验
     */
    public static final String VALID_SELECT_ALL = " ValidSelect : all";

    public static final String VALID_SELECT_PREFIX = " ValidSelect: ";


    /**
     * 提炼 javax 或 hibernate 的校验注解信息，返回给前端展示
     * <p>
     * 要提炼的注解必须存在 {@link Constraint }
     *
     * @param annotation 指定要提炼的注解,必须是 javax 的校验注解
     * @return 返回提炼的结果
     */
    public static String prepare(Annotation annotation) {
        if (annotation instanceof Email) {
            return "@Email";
        } else if (annotation instanceof Pattern) {
            String defaultMessage = "{javax.validation.constraints.Pattern.message}";

            String regexp = ((Pattern) annotation).regexp();
            String message = ((Pattern) annotation).message();

            return message.equals(defaultMessage) ? "@Pattern : regexp =" + regexp
                    : "@Pattern : regexp=" + regexp + " , message=" + message + "";
        } else if (annotation instanceof DomainRule) {
            String value = ((DomainRule) annotation).value();
            return "".equals(value) ? "@DomainRule" : "@DomainRule: " + value;
        }
        String str = annotation.toString();
        str = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));

        Optional<String> reduce = Arrays.stream(str.split(","))
                .filter(string -> !string.contains("groups"))
                .filter(string -> !string.contains("payload"))
                .filter(string -> !string.contains("message"))
                .reduce((str1, str2) -> str1 + str2);

        String prefix = "@" + annotation.annotationType().getSimpleName();
        return reduce.isPresent() ? prefix + " : " + reduce.get() : prefix;
    }

    /**
     * 针对 Domain 的属性生成对应的Schema
     */
    public static String fieldSchema(Field field) {
        StringBuilder sb = new StringBuilder();

        if (field.isAnnotationPresent(SchemaDocumentation.class)) {
            SchemaDocumentation annotation = field.getAnnotation(SchemaDocumentation.class);
            sb.append(annotation.value()).append(SEPARATOR);
        }
        Arrays.stream(field.getAnnotations())
                .filter(ann -> ann.annotationType().isAnnotationPresent(Constraint.class))
                .forEach(ann -> sb.append(prepare(ann)).append(SEPARATOR));
        return sb.toString();
    }


    public static String methodSchema(Method method, Parameter parameter) {
        if (method.getDeclaringClass().isAnnotationPresent(MutationValidated.class)) {
            return Arrays.stream(parameter.getAnnotations())
                    .filter(ann -> ann.annotationType().isAnnotationPresent(Constraint.class))
                    .map(SchemaParseUtil::prepare)
                    .reduce((annStr1, annStr2) -> annStr1 + " " + annStr2)
                    .orElse("");
        }
        return "";
    }
}
