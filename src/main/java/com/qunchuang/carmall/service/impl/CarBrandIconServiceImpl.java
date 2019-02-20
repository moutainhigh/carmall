package com.qunchuang.carmall.service.impl;


import com.qunchuang.carmall.domain.CarBrandIcon;
import com.qunchuang.carmall.repository.CarBrandIconRepository;
import com.qunchuang.carmall.service.CarBrandIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/30 10:02
 */
@Service
public class CarBrandIconServiceImpl implements CarBrandIconService {

    @Autowired
    private CarBrandIconRepository carBrandIconRepository;

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public CarBrandIcon save(CarBrandIcon carBrandIcon) {
        Optional<CarBrandIcon> optional = carBrandIconRepository.findByBrand(carBrandIcon.getBrand());
        if (optional.isPresent()) {
            CarBrandIcon result = optional.get();
            result.setIcon(carBrandIcon.getIcon());
            return carBrandIconRepository.save(result);
        }
        return carBrandIconRepository.save(carBrandIcon);
    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public List<CarBrandIcon> saveAll(List<CarBrandIcon> carBrandIcons) {
        //todo 做限制
        return carBrandIconRepository.saveAll(carBrandIcons);
    }
}
