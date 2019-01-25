package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.OrderStatus;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.ConsultRepository;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.service.ConsultService;
import com.qunchuang.carmall.service.CustomerService;
import com.qunchuang.carmall.service.StoreService;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/21 8:32
 */
@Slf4j
@Service
public class ConsultServiceImpl implements ConsultService {

    @Autowired
    private ConsultRepository consultRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminService adminService;

    @Override
    public Consult add(Consult consult) {

        //todo 如果之后咨询单生成过多 可以redis加锁限制咨询单生成


        //todo 验证码校验

        Consult rs = new Consult();
        String storeId;
        Customer customer;

        //todo 通过principal直接拿到用户   并绑定到咨询单  小程序完成认证后修改
        //现在先通过手机号查找用户(因为手机号存在用户换手机号问题 )  之后改为principal获取
        try {
            customer = customerService.findByPhone(consult.getPhone());
        } catch (CarMallException e) {
            customer = new Customer();

        }

        //用户还没有有所属门店
        if (StringUtils.isEmpty(customer.getStoreId())) {
            //用户还没绑定门店
            //1.用户未选择门店  则分配最近的
            if (consult.getStoreId() == null || "".equals(consult.getStoreId())) {
                //todo 现在先随机指定以后  后续改成经纬度
                storeId = storeService.getValidId();

            } else {
                //2.用户选择的门店
                //判断门店是否存在
                storeService.existsById(consult.getStoreId());
                storeId = consult.getStoreId();
            }

            //用户还未绑定手机号
            if (StringUtils.isEmpty(customer.getPhone())) {
                //绑定用户手机号  完成自动注册         //todo  不确定是否不可以不用注册
                customer.setPhone(consult.getPhone());
                customer.setStoreId(storeId);
                customer = customerService.modify(customer);
            }
        } else {
            //直接绑定到之前的门店
            storeId = customer.getStoreId();
        }

        rs.setStoreId(storeId);
        rs.setCustomer(customer);
        rs.setPhone(consult.getPhone());

        return consultRepository.save(rs);
    }

    @Override
    @PreAuthorize("hasAuthority('CUSTOMER_MANAGEMENT,SALES_CONSULTANT_MANAGEMENT')")
    public Consult allocate(String id, String salesId) {
        Consult consult = findOne(id);
        Admin admin;

        //权限只有所属门店才能派单
        Customer customer = consult.getCustomer();
        admin = getAdmin();
        if (!customer.getStoreId().equals(admin.getStoreId())) {
            log.error("派咨询单失败，该订单已不再所属此门店 customer.storeId() = %s, storeId() = %s", customer.getStoreId(), admin.getStoreId());
            throw new CarMallException(CarMallExceptionEnum.CONSULT_ALLOCATE_FAIL);
        }

        //判断销售员是否真的存在
        adminService.existsById(salesId);

        //如果客户还没有所属关系
        if (customer.getSalesConsultantId() == null || "".equals(customer.getSalesConsultantId())) {
            customer.setSalesConsultantId(salesId);
            customerService.modify(customer);
        }


        consult.setSalesConsultantId(salesId);
        return consultRepository.save(consult);
    }

    private Admin getAdmin() {
        Admin admin;
        try {
            admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("获取用户登录信息失败 Authentication = %s", SecurityContextHolder.getContext().getAuthentication());
            throw new CarMallException(CarMallExceptionEnum.PRIVILEGE_FAIL);
        }
        return admin;
    }

    @Override
    public Consult changeToStore(String id, String storeId) {

        Consult consult = findOne(id);
        //判断门店是否存在
        storeService.existsById(storeId);
        //修改订单的所属门店
        consult.setStoreId(storeId);
        //如果销售人员存在 则清空
        consult.setSalesConsultantId("");

        //修改用户的所属门店 及清空所属销售人员
        Customer customer = consult.getCustomer();
        customer.setStoreId(storeId);
        customer.setSalesConsultantId("");
        customerService.modify(customer);


        return consultRepository.save(consult);
    }

    @Override
    public Consult changeToSalesConsultant(String id, String salesId) {
        Consult consult = findOne(id);

        //判断销售员是否存在
        adminService.existsById(salesId);
        //修改订单的所属销售人员
        consult.setSalesConsultantId(salesId);

        //修改用户所属销售人员。
        Customer customer = consult.getCustomer();
        customer.setSalesConsultantId(salesId);
        customerService.modify(customer);

        return consultRepository.save(consult);
    }

    @Override
    public Consult delete(String id) {
        Consult consult = findOne(id);
        consult.isAble();
        return consultRepository.save(consult);
    }

    @Override
    public Consult finish(String id) {
        Consult consult = findOne(id);
        //todo 只有用户所属销售人员才能完结订单
        consult.setStatus(OrderStatus.FINISH.getCode());
        return consultRepository.save(consult);
    }

    @Override
    public Consult findOne(String id) {
        Optional<Consult> consult = consultRepository.findById(id);
        if (!consult.isPresent()) {
            log.error("咨询单未找到 id = ", id);
            throw new CarMallException(CarMallExceptionEnum.CONSULT_NOT_EXISTS);
        }
        return consult.get();
    }

    @Override
    public Consult modify(Consult consult) {
        Admin admin;
        admin = getAdmin();
        Consult result = findOne(consult.getId());
        Customer customer = customerService.findByPhone(consult.getPhone());
        if (!customer.getSalesConsultantId().equals(admin.getId())) {
            log.error("修改咨询单失败，该订单已不再所属此销售人员 customer.salesId = %s, salesId = %s", customer.getSalesConsultantId(), admin.getId());
            throw new CarMallException(CarMallExceptionEnum.CONSULT_MODIFY_FAIL);
        }
        //修改信息
        //todo  只有用户所属的管理员才能修改信息   需要判断操作人是否等价于Customer所属人

        BeanUtils.copyProperties(consult, result, BeanCopyUtil.getNullPropertyNames(consult));
        return consultRepository.save(result);
    }


}
