package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.CarInfo;
import com.qunchuang.carmall.domain.Store;
import com.qunchuang.carmall.service.StoreService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author Curtain
 * @date 2019/1/17 10:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class StoreControllerTest {

    @Autowired
    private StoreService storeService;

    @Test
    public void add() throws Exception {
        Store store = new Store();
        store.setName("大学城店");
        store.setAddress("温职");
        store.setPhone("18112345678");
        store.setPeople("张三");
        Assert.assertNotNull(storeService.add(store));
    }

    @Test
    public void modify() throws Exception {
        Store store = storeService.findOne("tQyJHm8NGYKdUj9FKH6j93S01");
        store.setName("茶山店");
        Assert.assertNotNull(storeService.modify(store));


    }

    @Test
    public void delete() throws Exception {
        Assert.assertNotNull(storeService.delete("tQyJHm8NGYKdUj9FKH6j93S01"));
    }

    @Test
    public void changeOrder() throws Exception {
    }

}