package com.qunchuang.carmall.graphql.query.dataprivilege;

import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterCombinator;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterOperator;

import java.util.HashMap;
import java.util.Map;

import static com.qunchuang.carmall.graphql.query.enums.QueryFilterOperator.*;

/**
 * PrivilegeConstraintUtil 的构建类
 * <p>
 * 用来更友好的构建 PrivilegeConstraintUtil .
 * 也是数据库中 通过 SpEL 添加约束的实现类。
 * <p>
 * 通过 SPEL 添加约束的例子 ： “#b.start('age','=',18).and('name','=','小明').or('phone','=','13966666666').end()”
 * <p>
 * 注意： 该类是线程不安全的。
 *
 * @author zzk
 * @date 2018/11/27
 */
public class QueryFilterBuilder {

    private static final Map<String, QueryFilterOperator> OPERATOR_MAP = new HashMap<>();

    static {
        OPERATOR_MAP.put("=", EQUEAL);
        OPERATOR_MAP.put(">", GREATTHAN);
        OPERATOR_MAP.put(">=", NOTLESSTHAN);
        OPERATOR_MAP.put("<", LESSTHAN);
        OPERATOR_MAP.put("<=", NOTGREATTHAN);
        OPERATOR_MAP.put("isnull", ISNULL);
        OPERATOR_MAP.put("isnotnull", ISNOTNULL);
    }


    private QueryFilter started;

    private QueryFilter currented;


    public QueryFilterBuilder start(String key, String operator, String value) {
        started = parseString(key, operator, value);
        currented = started;
        return this;
    }

    public QueryFilterBuilder or(String key, String operator, String value) {
        QueryFilter nexted = parseString(key, operator, value);
        currented.setCombinator(QueryFilterCombinator.OR);
        currented.setNext(nexted);
        currented = nexted;

        return this;
    }

    public QueryFilterBuilder and(String key, String operator, String value) {
        QueryFilter nexted = parseString(key, operator, value);
        currented.setCombinator(QueryFilterCombinator.AND);
        currented.setNext(nexted);
        currented = nexted;

        return this;
    }

    public QueryFilter end() {
        return started;
    }


    private static QueryFilter parseString(String key, String operator, String value) {
        operator = operator.toLowerCase().trim();
        if (OPERATOR_MAP.containsKey(operator)) {
            return new QueryFilter(key, OPERATOR_MAP.get(operator), value);
        }
        throw new IllegalArgumentException("约束表达式: " + "(" + key + "," + operator + "," + value + ")" + " 不正确");
    }
}
