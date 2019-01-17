package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.CarInfo;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.CarInfoRepository;
import com.qunchuang.carmall.service.CarInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return carInfoRepository.save(carInfo);
    }

    @Override
    public CarInfo delete(CarInfo carInfo) {
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
