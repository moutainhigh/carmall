package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.*;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.IntegralCategoryEnum;
import com.qunchuang.carmall.enums.IntegralEnum;
import com.qunchuang.carmall.enums.OrderStatus;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.ConsultRepository;
import com.qunchuang.carmall.service.*;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import com.qunchuang.carmall.utils.JiGuangMessagePushUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
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

    @Autowired
    private IntegralRecordService integralRecordService;

    @Override
    @Transactional
    public Consult add(Consult consult, String code, String invitedId) {

        //todo 如果之后咨询单生成过多 可以redis加锁限制咨询单生成


        //验证码校验
        verificationService.verify(consult.getPhone(), code);

        Consult rs = new Consult();
        Customer customer;
        Store store;
        String phone = consult.getPhone();

        boolean exists = customerService.existsByPhone(phone);
        if (!exists) {
            //此用户未注册

            //绑定用户手机号  完成自动注册
            customer = new Customer();
            customer.setPhone(phone);
            customer.setInvitedId(invitedId);
            customer = customerService.register(customer);

        } else {
            //用户已注册
            customer = customerService.findByPhone(phone);
        }

        //用户还没有有所属门店
        if (customer.getStore() == null) {
            //用户还没绑定门店
            //1.用户未选择门店  则分配最近的
            if (consult.getStore() == null || "".equals(consult.getStore().getId())) {
                //todo 现在先随机指定以后  后续改成经纬度  也许是前端拿到后 自己选择
                store = storeService.getValidId();

            } else {
                //2.用户选择的门店
                store = consult.getStore();

            }
        } else {
            //直接绑定到之前的门店
            store = customer.getStore();
        }

        //用户已经有所属的销售人员  直接绑定销售人员  绑定门店
        if (customer.getSalesConsultantAdmin() != null) {
            rs.setSalesConsultantAdmin(customer.getSalesConsultantAdmin());
            //订单修改为已派
            rs.setStatus(OrderStatus.ALLOCATE.getCode());
            store = customer.getSalesConsultantAdmin().getStore();
        }


        customer.setStore(store);
        customer = customerService.modify(customer);

        rs.setStore(store);
        rs.setCustomer(customer);
        rs.setPhone(phone);
        //订单绑定邀请人
        if (!StringUtils.isEmpty(invitedId)) {
            rs.setInvitedId(invitedId);
        }

        JiGuangMessagePushUtil.sendMessage(store.getStoreAdminId(), JiGuangMessagePushUtil.CONTENT);

        return consultRepository.save(rs);
    }

    @Override
    @PreAuthorize("hasAuthority('SALES_CONSULTANT_MANAGEMENT')")
    public Consult allocate(String id, String salesId) {
        //todo  订单有没有派给销售人员  可以用状态标识
        Consult consult = findOne(id);

        //如果订单已经所属 这提示不能重复派单
        if (consult.getSalesConsultantAdmin() != null && !"".equals(consult.getSalesConsultantAdmin().getId())) {
            throw new CarMallException(CarMallExceptionEnum.CONSULT_ALREADY_ALLOCATE);
        }

        //权限只有所属门店才能派单
        Customer customer = consult.getCustomer();
        Admin admin = Admin.getAdmin();
        if (!customer.getStore().getId().equals(admin.getStore().getId())) {
            log.error("派咨询单失败，该订单已不再所属此门店 customer.storeId() = {}, storeId() = {}", customer.getStore().getId(), admin.getStore().getId());
            throw new CarMallException(CarMallExceptionEnum.CONSULT_ALLOCATE_FAIL);
        }

        Admin salesConsultantAdmin = adminService.findOne(salesId);
        consult.setSalesConsultantAdmin(salesConsultantAdmin);

        //如果客户还没有所属关系
        if (customer.getSalesConsultantAdmin() == null || "".equals(customer.getSalesConsultantAdmin().getId())) {
            customer.setSalesConsultantAdmin(salesConsultantAdmin);
            customerService.modify(customer);
        }

        //订单状态改为派单
        consult.setStatus(OrderStatus.ALLOCATE.getCode());

        JiGuangMessagePushUtil.sendMessage(salesConsultantAdmin.getId(), JiGuangMessagePushUtil.CONTENT);


        return consultRepository.save(consult);
    }


    @Override
    @PreAuthorize("hasAuthority('STORE_MANAGEMENT')")
    public Consult changeToStore(String id, String storeId) {

        Consult consult = findOne(id);

        //修改订单的所属门店
        Store store = storeService.findOne(storeId);
        consult.setStore(store);
        //如果销售人员存在 则清空
        consult.setSalesConsultantAdmin(null);

        //修改用户的所属门店 及清空所属销售人员
        Customer customer = consult.getCustomer();
        customer.setStore(store);
        customer.setSalesConsultantAdmin(null);
        customerService.modify(customer);


        return consultRepository.save(consult);
    }

    @Override
    @PreAuthorize("hasAuthority('SALES_CONSULTANT_MANAGEMENT')")
    public Consult changeToSalesConsultant(String id, String salesId) {
        Consult consult = findOne(id);

        //修改订单的所属销售人员
        Admin salesConsultantAdmin = adminService.findOne(salesId);
        consult.setSalesConsultantAdmin(salesConsultantAdmin);

        //修改用户所属销售人员。
        Customer customer = consult.getCustomer();
        customer.setSalesConsultantAdmin(salesConsultantAdmin);
        customerService.modify(customer);

        return consultRepository.save(consult);
    }

    @Override
    public Consult delete(String id) {
        Consult consult = findOne(id);
        //只有用户所属销售人员才能完结订单
        belong2Sales(consult);
        consult.isAble();
        return consultRepository.save(consult);
    }

    @Override
    public Consult finish(String id) {
        Consult consult = findOne(id);
        //只有用户所属销售人员才能完结订单
        belong2Sales(consult);

        //如果订单是受邀请的 那么增加积分2000
        if (!StringUtils.isEmpty(consult.getInvitedId())) {
            Customer customer;
            try {
                customer = customerService.findOne(consult.getInvitedId());

                customer.modifyIntegral(IntegralEnum.FINISH_CONSULT.getCode());
                customer = customerService.save(customer);

                //记录保存
                IntegralRecord integralRecord = new IntegralRecord(IntegralCategoryEnum.INCREASE.getCode(),
                        IntegralEnum.FINISH_CONSULT.getCode(), IntegralRecord.FINISH_CONSULT, customer.getIntegral(), consult.getId(), customer.getId());

                integralRecordService.save(integralRecord);
            } catch (CarMallException e) {
                log.error("订单完结，积分增加失败，找不到受邀请的用户 customerId={}", consult.getInvitedId());
            }


        }

        if (consult.getStatus() == OrderStatus.ALLOCATE.getCode()) {
            consult.setStatus(OrderStatus.FINISH.getCode());
            return consultRepository.save(consult);
        }
        log.error("新订单不允许完结 id = {}", id);
        throw new CarMallException(CarMallExceptionEnum.CONSULT_NOT_FINISH);

    }

    private void belong2Sales(Consult consult) {
        Admin admin = Admin.getAdmin();
        Customer customer = consult.getCustomer();
        if (!customer.getSalesConsultantAdmin().getId().equals(admin.getId())) {
            log.error("修改咨询单失败，该订单已不再所属此销售人员 customer.salesId = {}, salesId = {}", customer.getSalesConsultantAdmin().getId(), admin.getId());
            throw new CarMallException(CarMallExceptionEnum.CONSULT_MODIFY_FAIL);
        }
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
        Consult result = findOne(consult.getId());
        //只有用户所属销售人员才能完结订单
        belong2Sales(result);

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
