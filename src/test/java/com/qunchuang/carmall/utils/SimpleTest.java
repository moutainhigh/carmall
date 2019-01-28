package com.qunchuang.carmall.utils;

import com.qunchuang.carmall.domain.Customer;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;

/**
 * @author Curtain
 * @date 2019/1/25 15:41
 */
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
        customer.setStoreId("store");
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
}
