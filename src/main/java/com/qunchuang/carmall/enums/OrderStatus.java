package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/1/24 10:39
 */
@Getter
public enum  OrderStatus {
    /*新订单*/
    NEW(0),
    /*完结*/
    FINISH(1);

    private Integer code;

    OrderStatus(Integer code){
        this.code = code;
    }
}
