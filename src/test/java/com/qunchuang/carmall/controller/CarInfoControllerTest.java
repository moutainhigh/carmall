package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.CarInfo;
import com.qunchuang.carmall.service.CarInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Curtain
 * @date 2019/1/17 10:03
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CarInfoControllerTest {

    @Autowired
    private CarInfoService carInfoService;


    @Test
    public void add() throws Exception {
        CarInfo carInfo = new CarInfo();
        carInfo.setBrand("玛莎拉蒂");
        carInfo.setModel("轿车系列");
        carInfo.setPrice("110万");
        Assert.assertNotNull(carInfoService.add(carInfo));
    }

    @Test
    public void modify() throws Exception {
        CarInfo carInfo = carInfoService.findOne("KWh690RLEoeHFIoDiWgbV0C02");
        carInfo.setPrice("120万");
        Assert.assertNotNull(carInfoService.modify(carInfo));


    }

    @Test
    public void delete() throws Exception {
        Assert.assertNotNull(carInfoService.delete("KWh690RLEoeHFIoDiWgbV0C02"));
    }

}