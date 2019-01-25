package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.service.CustomerService;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
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

//    @SchemaDocumentation("用户修改")
//    @GraphqlMutation(path = "/modify")
//    public Customer modify(Customer customer){
//        return customerService.modify(customer);
//    }

    @SchemaDocumentation("用户拉黑")
    @GraphqlMutation(path = "/delete")
    public Customer delete(String id){
        return customerService.delete(id);
    }

}
