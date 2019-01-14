package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Customer;

/**
 * @author Curtain
 * @date 2019/1/14 9:07
 */
public interface CustomerService {

    /**
     * 通过openid查询用户
     * @param openid
     * @return
     */
    Customer findByOpenid(String openid);

    /**
     * 保存
     * @param customer
     * @return
     */
    Customer save(Customer customer);
}
