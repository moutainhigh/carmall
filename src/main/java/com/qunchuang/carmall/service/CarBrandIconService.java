package com.qunchuang.carmall.service;


import com.qunchuang.carmall.domain.CarBrandIcon;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/1/30 9:58
 */
public interface CarBrandIconService {

    /**
     * 保存一条信息 如果存在则覆盖
     * @param carBrandIcon
     * @return
     */
    CarBrandIcon save(CarBrandIcon carBrandIcon);

    /**
     * 保存所有
     * @param carBrandIcons
     */
    List<CarBrandIcon> saveAll(List<CarBrandIcon> carBrandIcons);

    /**
     * 品牌是否存在
     * @param brand
     * @return
     */
    boolean existsByBrand(String brand);
}
