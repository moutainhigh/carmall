package com.qunchuang.carmall.auth.phone;


import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 处理用户信息
 *
 * @author Curtain
 * @date 2018/11/8:35
 */

@Component
public class PhoneUserInfo {

    @Autowired
    private CustomerService customerService;

    public Customer getCustomer(Map<String, String> data) {

        boolean phone = customerService.existsByPhone(data.get("phone"));
        if (phone) {
            //已注册直接返回
            return customerService.findByPhone(data.get("phone"));
        } else {
            Customer customer = new Customer();
            customer.setPhone(data.get("phone"));
            customer.setInvitedId(data.get("invitedId"));
            return customerService.register(customer);
        }

    }
}