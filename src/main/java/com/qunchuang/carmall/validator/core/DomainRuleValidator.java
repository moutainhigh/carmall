package com.qunchuang.carmall.validator.core;

import cn.wzvtcsoft.validator.anntations.DomainRule;
import cn.wzvtcsoft.validator.core.RuleParser;
import cn.wzvtcsoft.validator.exceptions.DomainRuleCheckException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.wzvtcsoft.validator.core.RuleParser.executeStringValid;

public class DomainRuleValidator implements ConstraintValidator<DomainRule, Object> {


    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();


    private String rule;


    @Override
    public void initialize(DomainRule domainRule) {
        rule = domainRule.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (BeanUtils.isSimpleValueType(value.getClass())) {
            throw new DomainRuleCheckException("@DomainRule 注解不能使用在简单类型及其包装类和String上");
        }
        context.disableDefaultConstraintViolation();
        return validDetail(rule, value, context);
    }

    /**
     * 具体的校验细节
     *
     * @param rule    @DomainRule 中的校验规则
     * @param object  要校验的对象
     * @param context 校验上下文
     * @return true表示校验成功，false 表示校验失败
     */
    private static boolean validDetail(String rule, Object object, ConstraintValidatorContext context) {
        if ("".equals(rule)) {
            Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(object);
            return parseViolations(violations, context);
        } else {
            List<String> properties = RuleParser.getProperties(rule);
            BeanWrapper srcBean = new BeanWrapperImpl(object);

            List<Boolean> propertyValidResults = properties
                    .stream()
                    .map(property -> validProperty(property, srcBean, context))
                    .collect(Collectors.toList());

            return executeStringValid(rule, properties, propertyValidResults);
        }

    }


    /**
     * 对校验未通过的属性进行信息封装.
     * 判断校验是否通过，若校验未通过则将未通过的属性进行封装.
     *
     * @param violations 校验结果
     * @param context    封装的容器
     * @return 校验通过返回 true, 校验失败返回false
     */
    private static boolean parseViolations(Set<ConstraintViolation<Object>> violations,
                                           ConstraintValidatorContext context) {
        if (violations.isEmpty()) {
            return true;
        } else {
            violations.forEach(violation -> {
                String name = violation.getPropertyPath().toString();
                context.buildConstraintViolationWithTemplate(violation.getMessage())
                        .addPropertyNode(name)
                        .addConstraintViolation();
            });
            return false;
        }
    }


    /**
     * 对复杂对象的属性进行验证
     *
     * @param property 要验证的属性
     * @param srcBean  复杂对象
     * @param context  验证失败的内容返回
     * @return 通过返回true, 不通过返回false
     */
    private static boolean validProperty(String property, BeanWrapper srcBean, ConstraintValidatorContext context) {
        Object propertyValue = srcBean.getPropertyValue(property);
        boolean isNull = propertyValue == null;

        if (isNull) {
            context.buildConstraintViolationWithTemplate(property + " 不能为 null ")
                    .addPropertyNode(property)
                    .addConstraintViolation();
            return false;
        }
        // 因为 Validator validateProperty() 方法对 存在 @Valid 注解的属性无效
        Set<ConstraintViolation<Object>> violations =
                VALIDATOR.validateProperty(srcBean.getWrappedInstance(), property);
        if (violations.isEmpty()) {
            if (BeanUtils.isSimpleValueType(srcBean.getWrappedClass())) {
                violations = VALIDATOR.validateProperty(srcBean.getWrappedInstance(), property);
            } else {
                violations = VALIDATOR.validate(propertyValue);
            }
        }


        return parseViolations(violations, context);
    }

}
