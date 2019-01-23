package cn.wzvtcsoft.validator.exceptions;

import cn.wzvtcsoft.validator.errors.ValidSelectError;

/**
 * 校验未通过时抛出的异常
 *
 * @author zzk
 * @date 2018/10/2
 */
public class MutationValidateException extends RuntimeException {

    private ValidSelectError error;

    private String message;

    public MutationValidateException(ValidSelectError error) {
        super(error.toString());
        this.error = error;
        this.message = error.getMessage();
    }

    public ValidSelectError getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }
}