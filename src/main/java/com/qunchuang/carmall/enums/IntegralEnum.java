package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/3/6 8:58
 */
@Getter
public enum IntegralEnum {
    /*新用户邀请注册*/
    REGISTER(2),
    /*咨询单完结*/
    FINISH_CONSULT(2000),
    /*积分上限10000*/
    UPPER_LIMIT(10000);

    private int code;

    IntegralEnum(int code){
        this.code = code;
    }
}
