package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.service.ConsultService;
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
 * @date 2019/1/21 9:56
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ConsultControllerTest {

    @Autowired
    private ConsultService consultService;

    @Test
    public void consult() throws Exception {
        Consult consult = new Consult();
        consult.setPhone("123456789123");
        Assert.assertNotNull(consultService.add(consult));
    }

    @Test
    public void allocate() throws Exception {
        Assert.assertNotNull(consultService.allocate("WhJAeigeEoS1VCxKKqOPx0C03","white snake"));
    }

    @Test
    public void modify() throws Exception {
        Consult consult = new Consult();
        consult.setId("WhJAeigeEoS1VCxKKqOPx0C03");
        consult.setName("小里");
        consult.setRemark("喜欢xx车");
        Assert.assertNotNull(consultService.modify(consult));
    }

}