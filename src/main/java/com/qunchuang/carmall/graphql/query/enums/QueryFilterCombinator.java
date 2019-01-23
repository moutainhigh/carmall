package com.qunchuang.carmall.graphql.query.enums;

import cn.wzvtcsoft.bosdomain.annotations.EntityEnum;
import cn.wzvtcsoft.bosdomain.enums.BosEnum;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;

/**
 * QueryFilterCombinator
 *
 * @author zzk
 * @date 2018/10/25
 */
@SchemaDocumentation("查询表达式组合操作符")
@EntityEnum
public enum QueryFilterCombinator implements BosEnum {

    /**
     * 逻辑的 and 连接
     */
    @EntityEnum(value = "AND", alias = "and")
    AND,

    /**
     * 逻辑的 or 连接
     */
    @EntityEnum(value = "OR", alias = "or")
    OR,

    /**
     * 逻辑的 not 连接
     */
    @EntityEnum(value = "NOT", alias = "!")
    NOT

}
