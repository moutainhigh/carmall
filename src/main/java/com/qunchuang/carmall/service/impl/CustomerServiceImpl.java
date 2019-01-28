package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.CustomerRepository;
import com.qunchuang.carmall.service.CustomerService;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        log.debug("用户未找到：openid = ", openid);
        throw new CarMallException(CarMallExceptionEnum.USER_NOT_EXISTS);
    }

    @Override
    public Customer share(Customer customer, String shareId) {
        return null;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return customerRepository.existsByPhone(phone);

    }

    @Override
    public Customer findByPhone(String phone) {
        Optional<Customer> customer = customerRepository.findByPhone(phone);
        if (!customer.isPresent()) {
            log.error("用户未找到，phone = {}", phone);
            throw new CarMallException(CarMallExceptionEnum.USER_NOT_EXISTS);
        }
        return customer.get();
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
    @PreAuthorize("hasAuthority('CUSTOMER_MANAGEMENT')")
    public Customer delete(String id) {

        Admin admin = Admin.getAdmin();
        Customer customer = findOne(id);
        //只能操作所属的客户
        if (!customer.getStoreId().equals(admin.getId()) && !customer.getSalesConsultantId().equals(admin.getId())) {
            log.error("权限不足，订单已经不属于当前用户 customerStoreId = {},customerSalesId = {}, adminId = {}",
                    customer.getStoreId(), customer.getSalesConsultantId(), admin.getId());
            throw new CarMallException(CarMallExceptionEnum.PRIVILEGE_INSUFFICIENT);
        }
        customer.isAble();
        return customerRepository.save(customer);
    }

    @Override
    public Customer modify(Customer customer) {
        Customer result = findOne(customer.getId());
        Set<String> filter = new HashSet<>();
        filter.add("integral");
        BeanUtils.copyProperties(customer, result, BeanCopyUtil.filterProperty(customer, filter));

        return customerRepository.save(result);
    }

    @Override
    public Customer register(Customer customer) {
        //todo  如果是被邀请 增加积分  或绑定销售顾问

        //因为小程序登录用户只有openid

        return customerRepository.save(customer);
    }
}
