package cn.wzvtcsoft.validator.core.impl;

import cn.wzvtcsoft.validator.core.MutationValidationMetaInfo;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MutationValidationMetaInfoImpl implements MutationValidationMetaInfo {

    private static final ParameterNameDiscoverer discoverer =
            new DefaultParameterNameDiscoverer();

    private Method method;


    private Object[] argumentObjects;

    private String[] argumentNames;

    private Object target;


    public MutationValidationMetaInfoImpl(Method method, Object[] argumentObjects, Object target) {
        this.method = method;
        this.argumentObjects = argumentObjects;
        this.target = target;

        argumentNames = discoverer.getParameterNames(method);

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < argumentNames.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                if (!"".equals(requestParam.name())) {
                    argumentNames[i] = requestParam.name();
                }
            }

        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String[] getArgumentNames() {
        return argumentNames;
    }


    @Override
    public Object[] getArgumentObjects() {
        return argumentObjects;
    }

    @Override
    public Object getTarget() {
        return target;
    }


}
