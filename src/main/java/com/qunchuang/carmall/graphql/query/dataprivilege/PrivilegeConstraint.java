package com.qunchuang.carmall.graphql.query.dataprivilege;

import java.lang.annotation.*;

/**
 * 表明是 权限约束 方法的注解。
 *
 * 该注解用来表示 Entity 中的 一个方法为权限约束方法。
 * 权限约束方法必须有以下的条件 :
 * 1. 方法必须是 静态公有的
 * 2. 方法的返回类型必须是 QueryFilter
 * 3. 方法不能拥有参数
 *<p>
 * @Entity
 * User Class{
 *
 *      @PrivilegeConstraint
 *         public static QueryFilter getQueryFilter() {
 *           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
 *             QueryFilter queryFilter;
 *             if (authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
 *
 *                 User user = (User) authentication.getPrincipal();
 *                 queryFilter = PrivilegeConstraintUtil.parseConstraint(user.getPredicate(), user);
 *
 *             } else {
 *                 queryFilter = new QueryFilter("username", QueryFilterOperator.EQUEAL, "ccc");
 *             }
 *             return queryFilter;
 *         }
 * }
 *   </p>
 * 使用案例:
 *
 *
 *
 * @author Curtain
 * @date 2018/10/26 8:58
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrivilegeConstraint {


}
