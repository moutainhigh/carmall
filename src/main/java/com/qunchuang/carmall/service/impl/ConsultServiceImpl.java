package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.OrderStatus;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.ConsultRepository;
import com.qunchuang.carmall.service.*;
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

    @Autowired
    private VerificationService verificationService;

    @Override
    public Consult add(Consult consult, String code) {

        //todo 如果之后咨询单生成过多 可以redis加锁限制咨询单生成


        //验证码校验
        verificationService.verify(consult.getPhone(), code);

        Consult rs = new Consult();
        String storeId;


        //通过principal直接拿到用户  (todo 此时用户可能是没有注册的  没有绑定手机号 具体注册逻辑再确定)
        Customer customer = Customer.getCustomer();

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
                //绑定用户手机号  完成自动注册         //todo 当前默认自动绑定手机号 为 咨询时手机号
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
    @PreAuthorize("hasAuthority('SALES_CONSULTANT_MANAGEMENT')")
    public Consult allocate(String id, String salesId) {
        Consult consult = findOne(id);

        //todo 如果订单已经派送 这提示不能重复派单

        //权限只有所属门店才能派单
        Customer customer = consult.getCustomer();
        Admin admin = Admin.getAdmin();
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
        //只有用户所属销售人员才能完结订单
        Admin admin = Admin.getAdmin();
        Customer customer = consult.getCustomer();
        if (!customer.getSalesConsultantId().equals(admin.getId())) {
            log.error("修改咨询单失败，该订单已不再所属此销售人员 customer.salesId = %s, salesId = %s", customer.getSalesConsultantId(), admin.getId());
            throw new CarMallException(CarMallExceptionEnum.CONSULT_MODIFY_FAIL);
        }
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
        Admin admin = Admin.getAdmin();
        Consult result = findOne(consult.getId());
        Customer customer = result.getCustomer();
        //判断订单是否所属为当前操作的销售人员
        if (!customer.getSalesConsultantId().equals(admin.getId())) {
            log.error("修改咨询单失败，该订单已不再所属此销售人员 customer.salesId = %s, salesId = %s", customer.getSalesConsultantId(), admin.getId());
            throw new CarMallException(CarMallExceptionEnum.CONSULT_MODIFY_FAIL);
        }
        //修改信息
        Set<String> filter = new HashSet<>();
        filter.add("customer");
        filter.add("status");
        filter.add("storeId");
        filter.add("salesConsultantId");
        BeanUtils.copyProperties(consult, result, BeanCopyUtil.filterProperty(consult, filter));
        return consultRepository.save(result);
    }


}
