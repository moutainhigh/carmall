package cn.wzvtcsoft.validator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ValidatorConditionProperties.PREFIX)
public class ValidatorConditionProperties {

    public static final String PREFIX = "com.example";


    private boolean mutationValidatorEnable = true;

    public boolean isMutationValidatorEnable() {
        return mutationValidatorEnable;
    }

    public void setMutationValidatorEnable(boolean mutationValidatorEnable) {
        this.mutationValidatorEnable = mutationValidatorEnable;
    }
}
