package com.qunchuang.carmall.graphql.query.enums;

/**
 * QueryForWhatEnum
 *
 * @author zzk
 * @date 2018/10/28
 */
public enum QueryForWhatEnum {
    /**
     * 仅仅是为了统计记录条数count(distinct(id))
     */
    JUSTFORCOUNTBYDISTINCTID,
    /**
     * 仅仅是为了找出某一页中记录的ids
     */
    JUSTFORIDSINTHEPAGE,
    /**
     * 常规
     */
    NORMAL
}
