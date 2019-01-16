package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Store;
import com.qunchuang.carmall.repository.StoreRepository;
import com.qunchuang.carmall.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Curtain
 * @date 2019/1/16 11:18
 */
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public Store changeOrder(Store store) {
        //todo 门店能否店给销售员转单   因为如果门店删除后，所属销售员和订单
        return null;
    }

    @Override
    public Store delete(Store store) {
        return null;
    }

    @Override
    public Store modify(Store store) {
        return null;
    }

    @Override
    public Store add(Store store) {
        return null;
    }
}
