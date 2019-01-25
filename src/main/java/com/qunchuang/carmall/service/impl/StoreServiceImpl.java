package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Store;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.StoreRepository;
import com.qunchuang.carmall.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/16 11:18
 */
@Service
@Slf4j
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;


    @Override
    public Store delete(String id) {
        Store store = findOne(id);
        store.isAble();
        return storeRepository.save(store);
    }

    @Override
    public Store modify(Store store) {
        //todo 有时需要确保 重要字段 不被修改
        return storeRepository.save(store);
    }

    @Override
    public void existsById(String id) {
        //判断门店是否存在
        boolean store = storeRepository.existsById(id);
        if (!store) {
            log.error("门店不存在 storeId = %s ", id);
            throw new CarMallException(CarMallExceptionEnum.STORE_NOT_EXISTS);
        }
    }

    @Override
    public String getValidId() {
        List<Store> all = storeRepository.findAll();
        if (all.size() <= 0) {
            throw new RuntimeException("一个门店也没有");
        }
        return all.get(0).getId();
    }

    @Override
    public Store findOne(String id) {
        Optional<Store> store = storeRepository.findById(id);
        if (!store.isPresent()) {
            log.error("");
            throw new CarMallException(CarMallExceptionEnum.STORE_NOT_EXISTS);
        }
        return store.get();
    }

    @Override
    public Store add(Store store) {
        return storeRepository.save(store);
    }
}
