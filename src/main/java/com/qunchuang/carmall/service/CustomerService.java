package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.domain.Store;

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
     * 销售顾问创建
     * @param customer
     * @return
     */
    Customer createSalesConsultant(Customer customer);

    /**
     * 用户注册
     * @param customer
     * @return
     */
    Customer register(Customer customer);

    /**
     * 分享
     * @param customer
     * @param shareId
     * @return
     */
    Customer share(Customer customer,String shareId);

    /**
     * 用户信息修改
     * @param customer
     * @return
     */
    Customer modify(Customer customer);

    /**
     * 删除用户
     * @param id
     * @return
     */
    Customer delete(String id);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Customer findOne(String id);
}
