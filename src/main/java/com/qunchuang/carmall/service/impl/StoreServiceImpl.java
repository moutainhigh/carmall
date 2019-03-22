package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.domain.Store;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.StoreRepository;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.service.CustomerService;
import com.qunchuang.carmall.service.StoreService;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import com.qunchuang.carmall.utils.LocationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Curtain
 * @date 2019/1/16 11:18
 */
@Service
@Slf4j
@Transactional
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminService adminService;


    @Override
    @PreAuthorize("hasAuthority('STORE_MANAGEMENT')")
    public Store delete(String id) {
        Store store = findOne(id);
        store.isAble();
        //用户重新绑定
        List<Customer> customerList = customerService.findByStore(store);
        customerList.forEach(customer -> {
            customer.setStore(null);
            customer.setSalesConsultantAdmin(null);
        });
        customerService.saveAll(customerList);

        //门店下的 店长账号   销售人员账号也要假删除
        List<Admin> adminList = adminService.findByStore(store);
        adminList.forEach(admin -> {
            admin.delete();
        });

        adminService.saveAll(adminList);

        //店长账号
        return storeRepository.save(store);
    }

    @Override
    @PreAuthorize("hasAuthority('STORE_MANAGEMENT')")
    public Store modify(Store store) {
        Store result = findOne(store.getId());
        Set<String> filter = new HashSet<>();
        filter.add("storeAdminId");
        BeanUtils.copyProperties(store, result, BeanCopyUtil.filterProperty(store,filter));
        return storeRepository.save(result);
    }

    @Override
    public void initAccount(Store store) {
        store.setStoreAdminId("");
        storeRepository.save(store);
    }

    @Override
    public Store createAccount(Store store) {
//        Store rs = findOne(store.getId());
//        rs.setStoreAdminId(store.getStoreAdminId());
        return storeRepository.save(store);
    }

    @Override
    public Store nearestStore(Double latitude, Double longitude) {
        //遍历可用门店
        List<Store> allStore = storeRepository.findByDisabled(false);
        double min = 0.0;
        Store result = null;
        double distance = 0.0;

        for (int i = 0; i < allStore.size(); i++) {
            Store store = allStore.get(i);
            try {
                distance = LocationUtils.getDistance(latitude, longitude,
                        Double.valueOf(store.getLatitude()), Double.valueOf(store.getLongitude()));
            } catch (Exception e) {
                log.error("用户经纬度 latitude = {}，longitude = {}，门店信息 store = {}", latitude, longitude, store);
                throw new CarMallException(CarMallExceptionEnum.STORE_DISTANCE_CALC_FAIL);
            }
            if (min > distance || result == null) {
                min = distance;
                result = store;
            }
        }

        return result;
    }

    @Override
    public void existsById(String id) {
        //判断门店是否存在
        boolean store = storeRepository.existsById(id);
        if (!store) {
            log.error("门店不存在 storeId = {}", id);
            throw new CarMallException(CarMallExceptionEnum.STORE_NOT_EXISTS);
        }
    }

    @Override
    public Store getValidId() {
        List<Store> all = storeRepository.findAll();
        if (all.size() <= 0) {
            throw new RuntimeException("一个门店也没有");
        }
        return all.get(0);
    }

    @Override
    public Store findOne(String id) {
        Optional<Store> store = storeRepository.findById(id);
        if (!store.isPresent()) {
            log.error("门店不存在 id = {}",id);
            throw new CarMallException(CarMallExceptionEnum.STORE_NOT_EXISTS);
        }
        return store.get();
    }

    @Override
    @PreAuthorize("hasAuthority('STORE_MANAGEMENT')")
    public Store add(Store store) {
        return storeRepository.save(store);
    }
}
