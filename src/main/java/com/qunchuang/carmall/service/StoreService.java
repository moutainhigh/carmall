package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Store;
/**
 * @author Curtain
 * @date 2019/1/16 11:11
 */
public interface StoreService {
    /**
     * 删除
     * @param id
     * @return
     */
    Store delete(String id);

    /**
     * 修改
     * @param store
     * @return
     */
    Store modify(Store store);

    /**
     * 添加
     * @param store
     * @return
     */
    Store add(Store store);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Store findOne(String id);

    /**
     * 获取一个有效的Id  第一期接口使用  后续删除
     * @return
     */
    Store getValidId();

    /**
     * 通过id判断是否存在
     * @param id
     * @return
     */
    void existsById(String id);

    /**
     * 获取离用户最近的门店
     * @param latitude
     * @param longitude
     * @return
     */
    Store nearestStore(Double latitude,Double longitude);
}
