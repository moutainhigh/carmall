package com.qunchuang.carmall.utils;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterOperator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Curtain
 * @date 2019/2/18 14:19
 */
public class PrivilegeUtil {

    /**
     * 获取Principal
     *
     * @return
     */
    public static Object getPrincipal() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BadCredentialsException("权限不足");
        }
    }

    public static QueryFilter customerInfoPrivilege() {
        QueryFilter queryFilter = null;
        Object principal;

        principal = getPrincipal();

        if (principal instanceof Admin) {
            Admin admin = (Admin) principal;

            if (admin.storeAdmin()) {
                //门店管理员 查看门店所属的
                queryFilter = new QueryFilter("store.id", QueryFilterOperator.EQUEAL, admin.getStore().getId());
            } else if (admin.salesAdmin()) {
                //销售管理员 查看销售所属的
                queryFilter = new QueryFilter("salesConsultantAdmin.id", QueryFilterOperator.EQUEAL, admin.getId());
            } else if (admin.superAdmin()) {
                //超级管理员 查看所有
                return queryFilter;
            } else {
                return queryFilter;
                //是否需要平台管理员查看咨询单。
            }


        } else if (principal instanceof Customer) {
            //客户查看客户所属的
            Customer customer = (Customer) principal;
            queryFilter = new QueryFilter("customer.id", QueryFilterOperator.EQUEAL, customer.getId());
        } else {
            //匿名用户 不允许查看
            throw new BadCredentialsException("权限不足");
        }

        return queryFilter;
    }

    public static QueryFilter integralRecordPrivilege() {
        QueryFilter queryFilter = null;
        Object principal;

        principal = getPrincipal();

        if (principal instanceof Admin) {
            Admin admin = (Admin) principal;
            if (admin.storeAdmin()) {
                //门店管理员 不能查看
                throw new AccessDeniedException("权限不足");
            } else if (admin.salesAdmin()) {
                //销售管理员 不能查看
                throw new AccessDeniedException("权限不足");
            } else if (admin.superAdmin()) {
                //超级管理员 查看所有
                return queryFilter;
            } else {
                return queryFilter;
                //是否需要平台管理员查看咨询单。
            }
        } else if (principal instanceof Customer) {
            //客户查看客户所属的
            Customer customer = (Customer) principal;
            queryFilter = new QueryFilter("customerId", QueryFilterOperator.EQUEAL, customer.getId());
        } else {
            //匿名用户 不允许查看
            throw new BadCredentialsException("未登录");
        }

        return queryFilter;
    }

}
