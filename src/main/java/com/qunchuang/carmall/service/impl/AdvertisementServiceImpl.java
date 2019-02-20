package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Advertisement;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.AdvertisementRepository;
import com.qunchuang.carmall.service.AdvertisementService;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/2/19 8:54
 */
@Service
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public Advertisement add(Advertisement advertisement) {
        return advertisementRepository.save(advertisement);
    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public Advertisement modify(Advertisement advertisement) {
        Advertisement result = findOne(advertisement.getId());
        BeanUtils.copyProperties(advertisement, result, BeanCopyUtil.filterProperty(advertisement));
        return advertisementRepository.save(result);

    }

    @Override
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public Advertisement delete(String id) {
        Advertisement advertisement = findOne(id);
        advertisement.isAble();
        //禁用隐藏
        return advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement findOne(String id) {
        Optional<Advertisement> advertisement = advertisementRepository.findById(id);
        if (!advertisement.isPresent()){
            log.error("广告不存在，id = {}",id);
            throw new CarMallException(CarMallExceptionEnum.ADVERTISEMENT_NOT_EXISTS);
        }
        return advertisement.get();
    }
}
