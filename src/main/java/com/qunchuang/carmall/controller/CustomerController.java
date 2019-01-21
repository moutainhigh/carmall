package com.qunchuang.carmall.controller;

import cn.wzvtcsoft.validator.anntations.DomainRule;
import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.domain.Store;
import com.qunchuang.carmall.service.ConsultService;
import com.qunchuang.carmall.service.CustomerService;
import graphql.annotation.GraphqlController;
import graphql.annotation.GraphqlMutation;
import graphql.annotation.SchemaDocumentation;
import org.checkerframework.checker.units.qual.C;
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

    @SchemaDocumentation("用户修改")
    @GraphqlMutation(path = "/modify")
    public Customer modify(Customer customer){
        return customerService.modify(customer);
    }

    @SchemaDocumentation("用户拉黑")
    @GraphqlMutation(path = "/delete")
    public Customer delete(String id){
        return customerService.delete(id);
    }

}
