package com.qunchuang.carmall.auth.phone;


import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Customer getCustomer(String phone) {
        Customer customer;
        try {
            customer = customerService.findByPhone(phone);
        } catch (Exception e) {
            // TODO: 2019/1/28 用户未注册   需要注册？
//            throw new BadCredentialsException("user not register");
            Customer rs = new Customer();
            rs.setPhone(phone);
            customer = customerService.register(rs);
        }
        return customer;
    }
}