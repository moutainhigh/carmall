package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.CarInfo;
import com.qunchuang.carmall.repository.CarInfoRepository;
import com.qunchuang.carmall.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2019/1/16 10:49
 */
@Service
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
        carInfo.delete();
        return carInfoRepository.save(carInfo);
    }
}
