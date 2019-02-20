package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.CarInfo;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.CarInfoRepository;
import com.qunchuang.carmall.service.CarInfoService;
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
 * @date 2019/1/16 10:49
 */
@Service
@Slf4j
public class CarInfoServiceImpl implements CarInfoService {

    @Autowired
    private CarInfoRepository carInfoRepository;

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public CarInfo add(CarInfo carInfo) {

        //取型号做唯一性区分
        String model = carInfo.getModel();
        if (carInfoRepository.existsByModel(model)) {
            //相同则覆盖
            CarInfo old = carInfoRepository.findByModel(model);
            Set<String> filter = new HashSet<>();
            filter.add("upperShelf");
            filter.add("financialSchemes");
            BeanUtils.copyProperties(carInfo, old, BeanCopyUtil.filterProperty(carInfo, filter));

            return carInfoRepository.save(old);
        }


        return carInfoRepository.save(carInfo);
    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public CarInfo modify(CarInfo carInfo) {

        CarInfo result = findOne(carInfo.getId());
        Set<String> filter = new HashSet<>();
        filter.add("upperShelf");
        filter.add("financialSchemes");
        BeanUtils.copyProperties(carInfo, result, BeanCopyUtil.filterProperty(carInfo, filter));

        //拷贝金融方案
        result.getFinancialSchemes().clear();
        result.getFinancialSchemes().addAll(carInfo.getFinancialSchemes());

        return carInfoRepository.save(result);
    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public CarInfo delete(String id) {
        CarInfo carInfo = findOne(id);
        carInfo.isAble();
        //删除时 下架
        carInfo.upperDownShelf();
        return carInfoRepository.save(carInfo);
    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public CarInfo upperDownShelf(String id) {
        CarInfo carInfo = findOne(id);
        carInfo.upperDownShelf();
        return carInfoRepository.save(carInfo);
    }

    @Override
    public CarInfo findOne(String id) {
        Optional<CarInfo> carInfo = carInfoRepository.findById(id);
        if (!carInfo.isPresent()) {
            log.error("车辆信息不存在 id = {}", id);
            throw new CarMallException(CarMallExceptionEnum.CAR_INFO_NOT_EXISTS);
        }
        return carInfo.get();
    }
}
