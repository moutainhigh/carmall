package com.qunchuang.carmall.exception;

import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import graphql.errors.BusinessException;
import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/1/14 8:57
 */

@Getter
public class CarMallException extends BusinessException {

    private CarMallExceptionEnum carMallExceptionEnum;

    public CarMallException(CarMallExceptionEnum anEnum){
        super(anEnum.getMessage(),anEnum.getCode());
        this.carMallExceptionEnum = anEnum;
    }
}
