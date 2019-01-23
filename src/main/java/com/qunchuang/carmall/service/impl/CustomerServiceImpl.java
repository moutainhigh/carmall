package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.CustomerRepository;
import com.qunchuang.carmall.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/14 9:09
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer findByOpenid(String openid) {
        Optional<Customer> optional = customerRepository.findByOpenid(openid);
        if (optional.isPresent()) {
            return optional.get();
        }
        log.error("用户未找到：openid = ", openid);
        throw new CarMallException(CarMallExceptionEnum.USER_NOT_EXISTS);
    }

    @Override
    public Customer createSalesConsultant(Customer customer) {
        //todo 创建销售顾问
        return customerRepository.save(customer);
    }

    @Override
    public Customer share(Customer customer, String shareId) {
        return null;
    }

    @Override
    public Customer findOne(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (!customer.isPresent()) {
            log.error("");
            throw new CarMallException(CarMallExceptionEnum.USER_NOT_EXISTS);
        }
        return customer.get();
    }

    @Override
    public Customer delete(String id) {
        Customer customer = findOne(id);
        customer.isAble();
        return customerRepository.save(customer);
    }

    @Override
    public Customer modify(Customer customer) {
        //todo 只有所属的门店或者所属的销售顾问才能修改   保证了转单后之前用户不能修改客户信息
        return customerRepository.save(customer);
    }

    @Override
    public Customer register(Customer customer) {
        //todo  如果是被邀请 增加积分  或绑定销售顾问

        Optional<Customer> optional = customerRepository.findByPhone(customer.getPhone());
        if (optional.isPresent()) {
            throw new CarMallException(CarMallExceptionEnum.USER_PHONE_IS_REGISTER);
        }


        return customerRepository.save(customer);
    }
}
