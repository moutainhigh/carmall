package com.qunchuang.carmall.controller;

import cn.wzvtcsoft.validator.anntations.DomainRule;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.service.CustomerService;
import graphql.annotation.GraphqlController;
import graphql.annotation.GraphqlMutation;
import graphql.annotation.SchemaDocumentation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2019/1/14 9:43
 */

@GraphqlController("customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @SchemaDocumentation("用户注册")
    @GraphqlMutation(path = "/register")
    public Customer register(Customer customer){
        return customerService.register(customer);
    }

    @SchemaDocumentation("用户咨询")
    @GraphqlMutation(path = "/consult")
    public Customer consult(){
        //todo 用户咨询时 还没有注册 算
        return null;
    }
}
