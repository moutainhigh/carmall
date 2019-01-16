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
        if (optional.isPresent()){
            return optional.get();
        }
        log.error("用户未找到：openid = ",openid);
       throw new CarMallException(CarMallExceptionEnum.USER_NOT_EXISTS);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
