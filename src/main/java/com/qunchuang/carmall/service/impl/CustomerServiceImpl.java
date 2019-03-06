package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.domain.IntegralRecord;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.IntegralCategoryEnum;
import com.qunchuang.carmall.enums.IntegralEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.CustomerRepository;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.service.CustomerService;
import com.qunchuang.carmall.service.IntegralRecordService;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Autowired
    private AdminService adminService;

    @Autowired
    private IntegralRecordService integralRecordService;

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
    public Customer share(Customer customer, String shareId) {
        return null;
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
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
            log.error("用户不存在 id = {}",id);
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
        if (!customer.getSalesConsultantAdmin().getId().equals(admin.getId())) {
            log.error("权限不足，订单已经不属于当前用户customerSalesId = {}, adminId = {}",
                    customer.getSalesConsultantAdmin().getId(), admin.getId());
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
        Customer result = new Customer();
        result.setPhone(customer.getPhone());

        //邀请人id
        String invitedId = customer.getInvitedId();
        if (!StringUtils.isEmpty(invitedId)){
            //邀请人是用户
            if (invitedId.endsWith("C01")){
                Customer invited = findOne(invitedId);
                invited.modifyIntegral(IntegralEnum.REGISTER.getCode());
                invited = customerRepository.save(invited);

                //记录保存
                IntegralRecord integralRecord = new IntegralRecord(IntegralCategoryEnum.INCREASE.getCode(),
                        IntegralEnum.REGISTER.getCode(),invited.getIntegral(),null,invited);

                integralRecordService.save(integralRecord);
            }
            //邀请人是销售人员
            if (invitedId.endsWith("A01")){
                result.setSalesConsultantAdmin(adminService.findOne(invitedId));
            }
        }

        result.modifyIntegral(IntegralEnum.REGISTER.getCode());
        result = customerRepository.save(result);

        IntegralRecord integralRecord = new IntegralRecord(IntegralCategoryEnum.INCREASE.getCode(),
                IntegralEnum.REGISTER.getCode(),result.getIntegral(),null,result);

        integralRecordService.save(integralRecord);

        return result;
    }
}
