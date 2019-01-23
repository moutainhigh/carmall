package cn.wzvtcsoft.validator.core.impl;

import cn.wzvtcsoft.validator.anntations.DomainRule;
import cn.wzvtcsoft.validator.anntations.ValidSelect;
import cn.wzvtcsoft.validator.core.MutationValidationMetaInfo;
import cn.wzvtcsoft.validator.core.MutationValidator;
import cn.wzvtcsoft.validator.core.RuleParser;
import cn.wzvtcsoft.validator.errors.ParamInfo;
import cn.wzvtcsoft.validator.errors.ValidSelectError;
import cn.wzvtcsoft.validator.exceptions.MutationValidateException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MutationValidatorImpl implements MutationValidator {

    private static final ExecutableValidator EXECUTABLE_VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

    private static final String VALID_SELECT_DEFAULT_MESSAGE = "请求参数校验未通过";

    @Override
    public void validate(MutationValidationMetaInfo metaInfo) {

        Method method = metaInfo.getMethod();
        Object[] paramValues = metaInfo.getArgumentObjects();
        String[] parameterNames = metaInfo.getArgumentNames();
        Object target = metaInfo.getTarget();


        ValidSelect validSelect = method.getAnnotation(ValidSelect.class);

        List<ParamInfo> paramInfoList = initParamInfo(target, method, paramValues, parameterNames);

        Optional<ValidSelectError> error;
        if (null == validSelect) {
            error = withoutValidSelectStrategy(paramInfoList);
        } else {
            error = hasValidSelectStrategy(paramInfoList, validSelect);
        }
        if (error.isPresent()) {
            throw new MutationValidateException(error.get());
        }
    }

    /**
     * 没有 @ValidSelect 的校验策略。
     * 实质是生成一个 ValidSelectError
     */
    private static Optional<ValidSelectError> withoutValidSelectStrategy(List<ParamInfo> paramInfoList) {
        paramInfoList.removeIf(ParamInfo::isPass);
        if (paramInfoList.isEmpty()) {
            return Optional.empty();

        }
        ValidSelectError error = new ValidSelectError(VALID_SELECT_DEFAULT_MESSAGE, paramInfoList);
        return Optional.of(error);
    }

    /**
     * 有 @ValidSelect 的校验策略。
     * 每一个 @ValidSelect 注解对应一个 ValidSelectError
     */
    private static Optional<ValidSelectError> hasValidSelectStrategy(List<ParamInfo> paramInfos,
                                                                     ValidSelect validSelect) {
        List<String> properties = RuleParser.getProperties(validSelect.value());

        String value = validSelect.value();
        List<Boolean> collect = paramInfos.stream().map(ParamInfo::isPass).collect(Collectors.toList());
        if (RuleParser.executeStringValid(value, properties, collect)) {
            return Optional.empty();
        } else {
            List<ParamInfo> errors = paramInfos.stream()
                    .filter(info -> !info.isPass())
                    .filter(info -> properties.contains(info.getName()))
                    .collect(Collectors.toList());
            String message = "".equals(validSelect.message()) ?
                    VALID_SELECT_DEFAULT_MESSAGE + ",不满足规则: " + validSelect.value() : validSelect.message();
            ValidSelectError validSelectError = new ValidSelectError(message, errors);
            return Optional.of(validSelectError);
        }
    }


    private static List<ParamInfo> initParamInfo(Object target,
                                                 Method method,
                                                 Object[] paramValues,
                                                 String[] parameterNames) {

        Set<ConstraintViolation<Object>> violations =
                EXECUTABLE_VALIDATOR.validateParameters(target, method, paramValues);

        List<ParamInfo> paramInfos = new ArrayList<>(parameterNames.length);
        for (int i = 0; i < parameterNames.length; i++) {
            Parameter parameter = method.getParameters()[i];

            String message = parameterNames[i] + " 校验未通过";
            if (parameter.isAnnotationPresent(DomainRule.class)) {
                DomainRule annotation = parameter.getAnnotation(DomainRule.class);
                if (!"".equals(annotation.value())) {
                    message += ",格式为 : " + annotation.value();
                }
                if (!"".equals(annotation.message())) {
                    message = annotation.message();
                }
            }
            paramInfos.add(new ParamInfo(parameterNames[i], paramValues[i], message));
        }

        violations.forEach(violation -> {
            String message = violation.getMessage();
            PathImpl path = (PathImpl) violation.getPropertyPath();
            int paramIndex = 0;

            if (path.getLeafNode().getKind() == ElementKind.PARAMETER) {
                paramIndex = path.getLeafNode().getParameterIndex();
                paramInfos.get(paramIndex).addError(message);

            } else {
                StringBuilder fieldBuilder = new StringBuilder();
                while (path.getLeafNode().getKind() != ElementKind.PARAMETER) {
                    fieldBuilder.insert(0, path.getLeafNode().getName())
                            .insert(0, ".");

                    path.removeLeafNode();

                }
                fieldBuilder.insert(0, parameterNames[paramIndex]);
                paramIndex = path.getLeafNode().getParameterIndex();

                paramInfos.get(paramIndex).addError(fieldBuilder.toString(), message);
            }

        });
        return paramInfos;
    }

}
