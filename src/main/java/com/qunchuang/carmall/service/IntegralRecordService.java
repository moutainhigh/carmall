package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.IntegralRecord;

/**
 * @author Curtain
 * @date 2019/3/6 9:54
 */
public interface IntegralRecordService {

    /**
     * 保存一条积分记录
     * @param integralRecord
     * @return
     */
    IntegralRecord save(IntegralRecord integralRecord);
}
