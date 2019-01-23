package com.qunchuang.carmall.graphql.query.enums;

import cn.wzvtcsoft.bosdomain.annotations.EntityEnum;
import cn.wzvtcsoft.bosdomain.enums.BosEnum;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;

/**
 * QueryFilterOperator
 * 用于 graphql 查询时使用
 *
 * @author zzk
 * @date 2018/10/25
 */ //TODO 可能需要扩展或者更规范化
@EntityEnum
@SchemaDocumentation("查询过滤操作符")
public enum QueryFilterOperator implements BosEnum {

    @EntityEnum(value = "ISNULL", alias = "为空")
    ISNULL,

    @EntityEnum(value = "ISNOTNULL", alias = "不为空")
    ISNOTNULL,

    @EntityEnum(value = "GREATTHAN", alias = "大于")
    GREATTHAN,

    @EntityEnum(value = "LESSTHAN", alias = "小于")
    LESSTHAN,

    @EntityEnum(value = "NOTLESSTHAN", alias = "不小于")
    NOTLESSTHAN,

    @EntityEnum(value = "NOTGREATTHAN", alias = "不大于")
    NOTGREATTHAN,

    @EntityEnum(value = "EQUEAL", alias = "相等")
    EQUEAL,

    @EntityEnum(value = "IN", alias = "包含")
    IN,

    @EntityEnum(value = "NOTIN", alias = "不包含")
    NOTIN,

    @EntityEnum(value = "NOT", alias = "非")
    NOT,

    @EntityEnum(value = "LIKE", alias = "LIKE")
    LIKE;

}
