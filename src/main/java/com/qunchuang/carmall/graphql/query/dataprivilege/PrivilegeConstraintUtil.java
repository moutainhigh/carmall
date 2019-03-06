package com.qunchuang.carmall.graphql.query.dataprivilege;

import com.qunchuang.carmall.graphql.query.QueryFilter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * PrivilegeConstraintUtil
 *
 * @author zzk
 * @date 2018/11/24
 */
public class PrivilegeConstraintUtil {

    private static SpelExpressionParser PARSER = new SpelExpressionParser();


    /**
     * 获得通过认证的对象
     *
     * @return 若对象通过认证，返回该对象；否则返回 null
     */
    public static Object getPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            return authentication.getPrincipal();
        }
        return null;
    }


    /**
     * 解析约束条件，生成 QueryFilter
     * <p>
     * 解析对 entity 的约束条件，并将解析结果以 QueryFilter 返回
     *
     * @param constraint 要解析的约束条件
     * @param principle  要解析的 principle 主体
     * @return 返回解析的 QueryFilter
     */
    public static QueryFilter parseConstraint(String constraint, Object principle) {
        if (principle == null) {
            throw new IllegalArgumentException("principle 不能为 null");
        }
        SpelExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("p", principle);
        context.setVariable("b", new QueryFilterBuilder());

        QueryFilter queryFilter = parser.parseExpression(constraint).getValue(context, QueryFilter.class);
        if (queryFilter == null) {
            throw new IllegalArgumentException("约束条件错误 : " + constraint);
        }


        PrivilegeConstraintUtil.parseQueryFilterValue(queryFilter, context);
        return queryFilter;
    }


    /**
     * 解析 PrivilegeConstraintUtil 中涉及到 SpEL 语句的 value 属性
     * <p>
     * 若 PrivilegeConstraintUtil 是来自 后端的约束，可能带有 SPEL 表达式，则进行解析
     *
     * @param queryFilter 指定要解析的 PrivilegeConstraintUtil
     * @param context     解析上下文
     */
    private static void parseQueryFilterValue(QueryFilter queryFilter, EvaluationContext context) {
        String value = queryFilter.getValue();
        if (value.startsWith("#p")) {
            value = PARSER.parseExpression(value).getValue(context, String.class);
            queryFilter.setValue(value);
        }
        if (queryFilter.getNext() != null) {
            parseQueryFilterValue(queryFilter.getNext(), context);
        }
    }


    /**
     * 合并已存在的 QueryFilter 和 新的 QueryFilter
     *
     * @param existQueryFilter 已存在
     * @param newQuertFilter   新生成的 QuertFilter
     */
    public static void merge(QueryFilter existQueryFilter, QueryFilter newQuertFilter) {
        if (existQueryFilter == null) {
            existQueryFilter = newQuertFilter;
        }
        if (newQuertFilter != null) {
            while (existQueryFilter.getNext() != null) {
                existQueryFilter = existQueryFilter.getNext();
            }
            existQueryFilter.setNext(newQuertFilter);
        }
    }


}
