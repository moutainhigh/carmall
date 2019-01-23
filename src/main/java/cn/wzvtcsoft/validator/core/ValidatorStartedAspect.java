package cn.wzvtcsoft.validator.core;

import cn.wzvtcsoft.validator.core.impl.MutationValidationMetaInfoImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * 校验启动切面
 * <p>
 * 当类上有 @Validated 注解时启动(一般是针对 @RestController)
 *
 * @author zzk
 * @date 2018/10/2
 */
@Component
@Aspect
public class ValidatorStartedAspect {

    private MutationValidator mutationValidator;

    public ValidatorStartedAspect(MutationValidator mutationValidator) {
        this.mutationValidator = mutationValidator;
    }


    @Before("@within(cn.wzvtcsoft.validator.anntations.MutationValidated)")
    private void valid(JoinPoint joinPoint) {
        Object obj = joinPoint.getThis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        MutationValidationMetaInfo info = new MutationValidationMetaInfoImpl(method, args, obj);
        mutationValidator.validate(info);
    }


}
