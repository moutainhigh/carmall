package com.qunchuang.carmall.exception;

import graphql.errors.BusinessException;

/**
 * @author Curtain
 * @date 2019/1/14 8:57
 */
public class CarMallException extends BusinessException {
    @Override
    public int getStatus() {
        return super.getStatus();
    }

    public CarMallException(String message, int status) {
        super(message, status);
    }

    public CarMallException(String message) {
        super(message);
    }
}
