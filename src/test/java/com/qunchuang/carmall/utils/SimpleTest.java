package com.qunchuang.carmall.utils;

import com.qunchuang.carmall.domain.Customer;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;

/**
 * @author Curtain
 * @date 2019/1/25 15:41
 */
@Slf4j
public class SimpleTest {

    @Test
    public void localVariable() {
//        int a;
//        System.out.println(a);

    }

    @Test
    public void BeanCopyUtils() {

        Customer customer = new Customer();
        customer.setId("111111111");
        customer.setName("haha");
        customer.setOpenid("xx");
        customer.setGender("xx");

        Customer rs = new Customer();

        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("name");
        hashSet.add("gender");
        BeanUtils.copyProperties(customer,rs,BeanCopyUtil.filterProperty(customer,hashSet));

        System.out.println(rs);

    }

    @Test
    public void log(){
        log.error("String = {}","xx");

        log.error("1={},2={},3={}","1","2","3");
    }
}
