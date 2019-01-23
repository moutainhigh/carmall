package com.qunchuang.carmall.graphql.query.enums;

import cn.wzvtcsoft.bosdomain.annotations.EntityEnum;
import cn.wzvtcsoft.bosdomain.enums.BosEnum;

@EntityEnum
public enum OrderByDirection implements BosEnum {

    @EntityEnum(value = "ASC", alias = "升序")
    ASC,
    @EntityEnum(value = "DESC", alias = "降序")
    DESC;

    public static final String ORDER_BY = "OrderBy";


}
