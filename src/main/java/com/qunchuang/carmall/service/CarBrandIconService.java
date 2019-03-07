package com.qunchuang.carmall.service;


import com.qunchuang.carmall.domain.CarBrandIcon;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/1/30 9:58
 */
public interface CarBrandIconService {

    /**
     * 保存所有
     * @param carBrandIcons
     */
    List<CarBrandIcon> saveAll(List<CarBrandIcon> carBrandIcons);

    /**
     * 导出车辆时初始化
     * @param carBrandIcons
     * @return
     */
    List<CarBrandIcon> initAll(List<CarBrandIcon> carBrandIcons);

    /**
     * 品牌是否存在
     * @param brand
     * @return
     */
    boolean existsByBrand(String brand);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    CarBrandIcon findOne(String id);

}
