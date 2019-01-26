package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.service.CustomerService;
import graphql.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Curtain
 * @date 2019/1/18 8:30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class CustomerControllerTest {

    @Autowired
    private CustomerService customerService;

    @Test
    public void register() throws Exception {
        Customer customer = new Customer();
        customer.setOpenid("xxxxxxxxxxxxxxxxx");
//        customer.setGender("女");
//        customer.setIntegral(0);
//        customer.setName("小月");
//        customer.setPhone("13812345678");
        Assert.assertNotNull(customerService.register(customer));
    }

    @Test
    public void consult() throws Exception {

    }

    @Test
    public void modify() throws Exception {
        Customer customer = customerService.findOne("LEVpYTiIHEuaaQa4VmNuw2C01");
        customer.setIntegral(1);
        Assert.assertNotNull(customerService.modify(customer));

    }

    @Test
    public void delete() throws Exception {
        Assert.assertNotNull(customerService.delete("LEVpYTiIHEuaaQa4VmNuw2C01"));
    }

}