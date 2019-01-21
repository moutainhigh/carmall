package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.CarInfo;
import com.qunchuang.carmall.domain.Store; /**
 * @author Curtain
 * @date 2019/1/16 11:11
 */
public interface StoreService {
    /**
     * 转单
     * @param store
     * @return
     */
    Store changeOrder(Store store);

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
    String getValidId();
}
