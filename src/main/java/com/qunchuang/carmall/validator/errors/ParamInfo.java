package com.qunchuang.carmall.validator.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 对要校验的方法的参数包装后的实例。
 * 1. 对即将要校验的方法上的所有参数包装成该实例集合
 * 2. 对校验未通过的方法上的参数实例输出
 *
 * @author zzk
 * @date 2018/10/2
 */
public class ParamInfo {

    @JsonIgnore
    private boolean pass = true;

    /**
     * 参数的实际名称
     */
    private String name;

    /**
     * 参数的错误信息
     */
    private String message;


    /**
     * 复杂对象的校验未通过的信息
     * 若检查未通过的对象是简单对象，则该属性为null.
     * 若是复杂对象，则 错误信息为 map 结构， key 为属性名， value 为错误信息
     */
    private Map<String, String> fieldErrorMap;

    public ParamInfo(String name, Object value, String message) {
        this.name = name;
        if (!BeanUtils.isSimpleValueType(value.getClass())) {
            fieldErrorMap = new HashMap<>();
            this.message = message;
        }

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPass() {
        return pass;
    }


    public String getName() {
        return name;
    }

    public void addError(String field, String message) {
        pass = false;
        fieldErrorMap.put(field, message);
    }

    public void addError(String message) {
        pass = false;
        this.message = message;
    }


    public Map<String, String> getFieldErrorMap() {
        if (fieldErrorMap == null || fieldErrorMap.isEmpty()) {
            return null;
        }
        return fieldErrorMap;
    }

}
