package com.qunchuang.carmall.service.impl;


import com.qunchuang.carmall.domain.CarBrandIcon;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.CarBrandIconRepository;
import com.qunchuang.carmall.service.CarBrandIconService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/30 10:02
 */
@Service
@Slf4j
public class CarBrandIconServiceImpl implements CarBrandIconService {

    @Autowired
    private CarBrandIconRepository carBrandIconRepository;

    @Override
    public boolean existsByBrand(String brand) {
        return carBrandIconRepository.existsByBrand(brand);
    }


    @Override
    public CarBrandIcon findOne(String id) {
        Optional<CarBrandIcon> carBrandIcon = carBrandIconRepository.findById(id);
        if (!carBrandIcon.isPresent()) {
            log.error("车辆品牌图标信息不存在 id = {}", id);
            throw new CarMallException(CarMallExceptionEnum.CAR_BRAND_ICON_NOT_EXISTS);
        }
        return carBrandIcon.get();
    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public List<CarBrandIcon> saveAll(List<CarBrandIcon> carBrandIcons) {

        List<CarBrandIcon> resultList = new ArrayList();
        CarBrandIcon result;

        for (CarBrandIcon carBrandIcon:carBrandIcons){
            result = findOne(carBrandIcon.getId());
            result.setChoose(carBrandIcon.isChoose());
            result.setIcon(carBrandIcon.getIcon());
            resultList.add(result);
        }

        return carBrandIconRepository.saveAll(resultList);
    }
}
