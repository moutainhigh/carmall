package com.qunchuang.carmall.auth.phone;


import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final static  String REGISTER = "1";
    private final static  String LOGIN = "2";


    @Autowired
    private CustomerService customerService;

    public Customer getCustomer(Map<String, String> data) {

        if (REGISTER.equals(data.get("type"))){
            boolean phone = customerService.existsByPhone(data.get("phone"));
            if (phone){
                throw new BadCredentialsException("user already exists");
            }
            Customer customer = new Customer();
            customer.setPhone(data.get("phone"));
            customer.setInvitedId(data.get("invitedId"));

            return customerService.register(customer);
        }

        //登录直接返回用户信息
        if (LOGIN.equals(data.get("type"))){
            return customerService.findByPhone(data.get("phone"));
        }



        throw new BadCredentialsException("格式错误");
    }
}