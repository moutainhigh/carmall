package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.ConsultRepository;
import com.qunchuang.carmall.service.ConsultService;
import com.qunchuang.carmall.service.StoreService;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public Consult add(Consult consult) {
        //todo 暂时分配到唯一的一个门店
        String storeId = storeService.getValidId();
        consult.setStoreId(storeId);
        return consultRepository.save(consult);
    }

    @Override
    public Consult allocate(String id,String salesId) {
        Consult consult = findOne(id);
        //todo 判断销售员是否真的存在
        consult.setSalesConsultantId(salesId);
        return consultRepository.save(consult);
    }

    @Override
    public Consult findOne(String id) {
        Optional<Consult> consult = consultRepository.findById(id);
        if (!consult.isPresent()){
            log.error("咨询单未找到 id = ",id);
            throw new CarMallException(CarMallExceptionEnum.CONSULT_NOT_EXISTS);
        }
        return consult.get();
    }

    @Override
    public Consult modify(Consult consult) {
        Consult result = findOne(consult.getId());
        //修改信息
        result.setName(consult.getName());
        result.setRemark(consult.getRemark());
        return consultRepository.save(result);
    }
}
