package com.qunchuang.carmall.auth.wechat;


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
        Customer customer;
        try {
            customer = customerService.findByOpenid(openid);
        } catch (Exception e) {
            //todo 考虑如果这里生成了 那下次用户才绑定手机号。
            Customer rs = new Customer();
            rs.setOpenid(openid);
            Customer result = customerService.register(rs);
            return result;
        }
        return customer;
    }
}