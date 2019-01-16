package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.privilege.Privilege;
import com.qunchuang.carmall.domain.privilege.PrivilegeItem;
import com.qunchuang.carmall.domain.privilege.Role;
import com.qunchuang.carmall.domain.privilege.RoleItem;
import com.qunchuang.carmall.enums.PrivilegeAuthorityEnum;
import com.qunchuang.carmall.repository.AdminRepository;
import com.qunchuang.carmall.repository.PrivilegeRepository;
import com.qunchuang.carmall.repository.RoleRepository;
import com.qunchuang.carmall.service.AdminService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Curtain
 * @date 2019/1/16 14:12
 */

@RunWith(SpringRunner.class)
@SpringBootTest
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
    @Transactional
    public void add() throws Exception {
        Admin admin = new Admin();

        admin.setUsername("admin");
        admin.setPassword("1");
        admin.setPhone("13512343422");

        PrivilegeItem privilegeItem1 = new PrivilegeItem();
        PrivilegeItem privilegeItem2 = new PrivilegeItem();
        PrivilegeItem privilegeItem3 = new PrivilegeItem();
        PrivilegeItem privilegeItem4 = new PrivilegeItem();

        Privilege privilege1 = new Privilege();
        privilege1.setPrivilege(PrivilegeAuthorityEnum.CUSTOMER_MANAGEMENT.getIdentifier());
        privilege1.setCategory(1);
        privilege1.setName("客户管理");
        Privilege privilege2 = new Privilege();
        privilege2.setPrivilege(PrivilegeAuthorityEnum.SALES_CONSULTANT_MANAGEMENT.getIdentifier());
        privilege2.setName("销售顾问管理");
        privilege2.setCategory(2);
        Privilege privilege3 = new Privilege();
        privilege3.setPrivilege(PrivilegeAuthorityEnum.VEHICLE_MANAGEMENT.getIdentifier());
        privilege3.setCategory(3);
        privilege3.setName("车辆管理");
        Privilege privilege4 = new Privilege();
        privilege4.setPrivilege(PrivilegeAuthorityEnum.STORE_MANAGEMENT.getIdentifier());
        privilege4.setCategory(4);
        privilege4.setName("门店管理");


        privilege1 = privilegeRepository.save(privilege1);
        privilege2 = privilegeRepository.save(privilege2);
        privilege3 = privilegeRepository.save(privilege3);
        privilege4 = privilegeRepository.save(privilege4);

        privilegeItem1.setPrivilege(privilege1);
        privilegeItem2.setPrivilege(privilege2);
        privilegeItem3.setPrivilege(privilege3);
        privilegeItem4.setPrivilege(privilege4);

        Role role1 = new Role();
        role1.setName("超级管理员");
        role1.getPrivilegeItems().add(privilegeItem1);
        role1.getPrivilegeItems().add(privilegeItem2);
        role1.getPrivilegeItems().add(privilegeItem3);
        role1.getPrivilegeItems().add(privilegeItem4);

        role1 = roleRepository.save(role1);

        RoleItem roleItem1 = new RoleItem();

        roleItem1.setRole(role1);

        admin.getRoleItems().add(roleItem1);

        //todo 这里如果不前保存 role 和 privilege 会出现以下错误   暂时未找到解决方案
        //todo object references an unsaved transient instance - save the transient instance before flushing

        Assert.assertNotNull(adminRepository.save(admin));
    }

    @Test
    public void modify() throws Exception {
    }

    @Test
    public void delete() throws Exception {
    }

}