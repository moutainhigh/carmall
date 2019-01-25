package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Consult;
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
import org.springframework.stereotype.Service;

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

        //todo 验证码校验

        Consult rs = new Consult();

        //用户未选择门店  则分配最近的
        if (consult.getStoreId() == null || "".equals(consult.getStoreId())) {
            //todo 现在先随机指定以后  后续改成经纬度
            String storeId = storeService.getValidId();
            rs.setStoreId(storeId);
        } else {
            rs.setStoreId(consult.getStoreId());
        }
        rs.setPhone(consult.getPhone());

        //todo 如果用户绑定手机号则需要绑定手机号

        //todo 用户已经有所属门店 则不能生成咨询到 到其他门店

//        //通过手机号绑定咨询用户，如果手机号未注册则默认创建
//        try {
//            customer = customerService.findByPhone(rs.getPhone());
//        }catch (CarMallException e){
//            //用户不存在  注册用户
//            customer.setPhone(rs.getPhone());
//            customerService.register()
//        }

        return consultRepository.save(consult);
    }

    @Override
    public Consult allocate(String id, String salesId) {
        Consult consult = findOne(id);
        //todo 权限只有所属门店才能派单
        //todo 判断销售员是否真的存在
        try {
            adminService.findOne(salesId);
        } catch (CarMallException e) {
            log.error("分配失败，销售人员不存在 salesId = %s", salesId);
            throw new CarMallException(CarMallExceptionEnum.SALES_CONSULTANT_NOT_EXISTS);
        }
        consult.setSalesConsultantId(salesId);
        return consultRepository.save(consult);
    }

    @Override
    public Consult changeToStore(String id, String storeId) {

        Consult consult = findOne(id);
        //todo 判断门店是否存在
        //修改订单的所属门店
        consult.setStoreId(storeId);
        //如果销售人员存在 则清空
        consult.setSalesConsultantId("");
        //todo 需要修改用户的所属门店

        return consultRepository.save(consult);
    }

    @Override
    public Consult changeToSalesConsultant(String id, String salesId) {
        Consult consult = findOne(id);
        //修改订单的所属销售人员
        //todo 需要判断是否存在
        consult.setSalesConsultantId(salesId);
        //todo 修改用户所属销售人员。

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
        Consult result = findOne(consult.getId());
        //修改信息
        //todo  只有用户所属的管理员才能修改信息   需要判断操作人是否等价于Customer所属人
        BeanUtils.copyProperties(consult, result, BeanCopyUtil.getNullPropertyNames(consult));
        return consultRepository.save(result);
    }




}
