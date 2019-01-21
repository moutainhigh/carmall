package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.privilege.Privilege;
import com.qunchuang.carmall.domain.privilege.PrivilegeItem;
import com.qunchuang.carmall.domain.privilege.Role;
import com.qunchuang.carmall.domain.privilege.RoleItem;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.PrivilegeAuthorityEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.AdminRepository;
import com.qunchuang.carmall.repository.PrivilegeRepository;
import com.qunchuang.carmall.repository.RoleRepository;
import com.qunchuang.carmall.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/16 9:39
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PrivilegeRepository privilegeRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (!admin.isPresent()) {
            log.error("登录失败，用户名不存在 username = ", username);
            throw new UsernameNotFoundException("user not exist");
        }
        return admin.get();
    }

    @Override
    public String init(String curtain) {
        if (curtain.equals("wcp")){
            Admin admin = new Admin();

            admin.setUsername("test");
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

            adminRepository.save(admin);
            return "初始化成功";
        }
        return "操作失败";
    }

    @Override
    public Admin delete(String id) {
        Admin admin = findOne(id);
        //将权限清空
        admin.setRoleItems(null);
        //将用户名无效化
        admin.setUsername("invalid username " + admin.getCreatetime());
        //拉黑
        admin.isAble();
        return adminRepository.save(admin);
    }

    @Override
    public Admin findOne(String id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (!admin.isPresent()) {
            log.error("查找管理员：用户不存在 id = ", id);
            throw new CarMallException(CarMallExceptionEnum.ADMIN_NOT_EXISTS);
        }
        return admin.get();
    }

    /**
     * 配置了修改权限  只有用户本身能修改 或者 包含B1(用户管理这个权限)
     *
     * @param admin
     * @return
     */
    @Override
//    @PreAuthorize("authenticated && (#user.id==authentication.principal.id || hasAuthority('B1'))")
    public Admin update(@P("user") Admin admin) {
        //todo 只有用户管理员可以修改   或者 自己改自己本身的    password 不能被修改
        //todo  超级管理员admin的权限和角色不允许修改
        admin.privilegeCheck();
        return adminRepository.save(admin);
    }

    @Override
    public Admin register(Admin admin) {
        //todo 分为提供 每个角色创建接头  隐藏具体细节

        Optional<Admin> result = adminRepository.findByUsername(admin.getUsername());
        if (result.isPresent()) {
            throw new CarMallException(CarMallExceptionEnum.USERNAME_IS_EXISTS);
        }
        Admin rs = new Admin();
        rs.setPhone(admin.getPhone());
        rs.setUsername(admin.getUsername());
        rs.setPassword(admin.getPassword());

        return adminRepository.save(rs);
    }

    @Override
//    @PreAuthorize("hasAuthority('B1')")
    public Admin save(Admin admin) {
        //todo 应该只保存  用户注册时应该 处理的数据
        //todo  管理员添加用户另外一个接口
        Optional<Admin> result = adminRepository.findByUsername(admin.getUsername());
        if (result.isPresent()) {
            log.error("账号创建失败，用户名已存在 username = ", admin.getUsername());
            throw new CarMallException(CarMallExceptionEnum.USERNAME_IS_EXISTS);
        }
        admin.privilegeCheck();
        return adminRepository.save(admin);
    }


}
