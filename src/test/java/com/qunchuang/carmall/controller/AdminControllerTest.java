package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.repository.AdminRepository;
import com.qunchuang.carmall.repository.PrivilegeRepository;
import com.qunchuang.carmall.repository.RoleRepository;
import com.qunchuang.carmall.service.AdminService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Curtain
 * @date 2019/1/16 14:12
 */

@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class AdminControllerTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PrivilegeRepository privilegeRepository;


    /**
     * 管理员添加测试  因为拿不到
     * @throws Exception
     */
    @Test
    public void add() throws Exception {
        Admin admin = new Admin();

        admin.setUsername("admin");
        admin.setPassword("1");
        admin.setPhone("13512322322");


        Assert.assertNotNull(adminRepository.save(admin));
    }

    @Test
    public void modify() throws Exception {
    }

    @Test
    public void delete() throws Exception {
        Assert.assertNotNull(adminService.delete("rKFZ19zLHNKbEiuqcBJ2n0A01"));
    }

}