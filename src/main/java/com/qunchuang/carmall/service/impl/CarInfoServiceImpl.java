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
    public CarInfo add(CarInfo carInfo) {
        return carInfoRepository.save(carInfo);
    }

    @Override
    public CarInfo modify(CarInfo carInfo) {

        CarInfo result = findOne(carInfo.getId());
        Set<String> filter = new HashSet<>();
        filter.add("upperShelf");
        BeanUtils.copyProperties(carInfo, result, BeanCopyUtil.filterProperty(carInfo, filter));

        return carInfoRepository.save(result);
    }

    @Override
    public CarInfo delete(String id) {
        CarInfo carInfo = findOne(id);
        carInfo.isAble();
        return carInfoRepository.save(carInfo);
    }

    @Override
    public CarInfo findOne(String id) {
        Optional<CarInfo> carInfo = carInfoRepository.findById(id);
        if (!carInfo.isPresent()){
            log.error("");
            throw new CarMallException(CarMallExceptionEnum.CAR_INFO_NOT_EXISTS);
        }
        return carInfo.get();
    }
}
