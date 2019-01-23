package com.qunchuang.carmall.validator;

import cn.wzvtcsoft.validator.ValidatorConditionProperties;
import cn.wzvtcsoft.validator.core.MutationValidator;
import cn.wzvtcsoft.validator.core.ValidatorStartedAspect;
import cn.wzvtcsoft.validator.core.impl.MutationValidatorImpl;
import cn.wzvtcsoft.validator.errors.MutationValidationHandler;
import cn.wzvtcsoft.validator.events.RuleValidCheckEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
public class ValidationAutoConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationAutoConfig.class);

    @Bean
    @ConditionalOnProperty(prefix = ValidatorConditionProperties.PREFIX, name = "mutation-validator-enable",
            havingValue = "true", matchIfMissing = true)
    public ValidatorStartedAspect validateAspect() {
        return new ValidatorStartedAspect(mutationValidator());
    }

    @Bean
    @ConditionalOnProperty(prefix = ValidatorConditionProperties.PREFIX, name = "mutation-validator-enable",
            havingValue = "true", matchIfMissing = true)
    public MutationValidator mutationValidator() {
        LOGGER.info("Started MutationValidator");
        return new MutationValidatorImpl();
    }

    @Bean
    @ConditionalOnBean(ValidatorStartedAspect.class)
    public RuleValidCheckEvent ruleValidEvent() {
        return new RuleValidCheckEvent();
    }

    @Bean
    @ConditionalOnBean(ValidatorStartedAspect.class)
    public MutationValidationHandler controllerValidationHandler() {
        return new MutationValidationHandler();
    }

}
