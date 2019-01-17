package com.qunchuang.carmall.auth;


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
public class WeChatMiniUserInfo {

    @Autowired
    private CustomerService customerService;

    public Customer getCustomer(String openid) {
        Customer customer = customerService.findByOpenid(openid);
        if (customer == null) {
            Customer rs = new Customer();
            rs.setOpenid(openid);
            Customer result = customerService.register(rs);
            return result;
        }
        return customer;
    }
}