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

    @SchemaDocumentation("添加用户")
    @GraphqlMutation(path = "/add")
    public Customer add(Customer customer){
        return customerService.save(customer);
    }
}
