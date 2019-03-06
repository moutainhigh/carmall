package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/3/6 9:46
 */
@Getter
public enum IntegralCategoryEnum {
    /**
     * 增加
     */
    INCREASE(1),
    /**
     * 扣除
     */
    REDUCE(2);

    private Integer code;

    IntegralCategoryEnum(Integer code) {
        this.code = code;
    }
}
