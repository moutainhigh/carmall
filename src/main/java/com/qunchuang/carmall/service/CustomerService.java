package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.domain.Store;

import java.util.List;

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

    /**
     * 通过手机号查找用户
     * @param phone
     * @return
     */
    Customer findByPhone(String phone);

    /**
     * 用手机号判断 用户是否存在
     * @param phone
     * @return
     */
    boolean existsByPhone(String phone);

    /**
     * 保存
     * @param customer
     * @return
     */
    Customer save(Customer customer);

    /**
     * 积分消费
     * @param customerId
     * @param integral
     * @param content
     * @return
     */
    Customer consumerIntegral(String customerId,Integer integral,String content);

    /**
     * 通过门店查找用户
     * @param store
     * @return
     */
    List<Customer> findByStore(Store store);

    /**
     * 保存所有
     * @param customers
     */
    void saveAll(List<Customer> customers);
}
