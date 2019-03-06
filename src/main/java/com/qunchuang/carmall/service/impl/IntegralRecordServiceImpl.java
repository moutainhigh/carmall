package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.IntegralRecord;
import com.qunchuang.carmall.repository.IntegralRecordRepository;
import com.qunchuang.carmall.service.IntegralRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2019/3/6 9:54
 */
@Service
public class IntegralRecordServiceImpl implements IntegralRecordService {

    @Autowired
    private IntegralRecordRepository integralRecordRepository;


    @Override
    public IntegralRecord save(IntegralRecord integralRecord) {
        return integralRecordRepository.save(integralRecord);
    }
}
