package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.CarInfo; /**
 * @author Curtain
 * @date 2019/1/16 10:29
 */
public interface CarInfoService {
    /**
     * 添加车辆
     * @param carInfo
     * @return
     */
    CarInfo add(CarInfo carInfo);

    /**
     * 修改车辆
     * @param carInfo
     * @return
     */
    CarInfo modify(CarInfo carInfo);

    /**
     * 删除车辆
     * @param id
     * @return
     */
    CarInfo delete(String id);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    CarInfo findOne(String id);
}

