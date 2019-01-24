package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Consult;

/**
 * @author Curtain
 * @date 2019/1/21 8:18
 */
public interface ConsultService {

    /**
     * 创建咨询单
     * @param consult
     * @return
     */
    Consult add(Consult consult);

    /**
     * 派单
     * @param id
     * @param salesId 销售员id
     * @return
     */
    Consult allocate(String id,String salesId);

    /**
     * 修改信息
     * @param consult
     * @return
     */
    Consult modify(Consult consult);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Consult findOne(String id);

    /**
     * 转单到门店
     * @param id
     * @param storeId
     * @return
     */
    Consult changeToStore(String id, String storeId);

    /**
     * 转单到销售人员
     * @param id
     * @param salesId
     * @return
     */
    Consult changeToSalesConsultant(String id, String salesId);

    /**
     * 订单完结
     * @param id
     * @return
     */
    Consult finish(String id);

}
