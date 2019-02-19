package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.domain.privilege.Privilege;
import com.qunchuang.carmall.domain.privilege.PrivilegeItem;
import com.qunchuang.carmall.domain.privilege.Role;
import com.qunchuang.carmall.domain.privilege.RoleItem;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.PrivilegeAuthorityEnum;
import com.qunchuang.carmall.enums.RoleEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.AdminRepository;
import com.qunchuang.carmall.repository.PrivilegeRepository;
import com.qunchuang.carmall.repository.RoleRepository;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    @PreAuthorize("hasAuthority('STORE_MANAGEMENT')")
    public Admin storeAdministrator(Admin admin) {
        return register(admin, RoleEnum.STORE_ADMINISTRATOR.getRoleName());
    }

    @Override
    public void existsById(String id) {
        boolean sales = adminRepository.existsById(id);
        if (!sales) {
            log.error("分配失败，销售人员不存在 salesId = {}", id);
            throw new CarMallException(CarMallExceptionEnum.SALES_CONSULTANT_NOT_EXISTS);
        }
    }

    @Override
    public Admin changePassword(String username, String password) {
        //超级管理员改所有 门店管理员改旗下的销售员
        Admin operator = Admin.getAdmin();
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (!adminOptional.isPresent()) {
            log.error("用户不存在，用户名 = {}", username);
            throw new CarMallException(CarMallExceptionEnum.ADMIN_NOT_EXISTS);
        }
        Admin admin = adminOptional.get();

        //是否是超级管理员
        boolean isSuperAdmin = operator.superAdmin();

        //是否是门店管理员 并修改的是所属账号
        boolean isStoreAdmin = operator.storeAdmin() && operator.getStore().getId().equals(admin.getStore().getId());

        //符合修改条件
        if (isSuperAdmin || isStoreAdmin) {
            //todo 加密
            admin.setPassword(password);
//            admin.setPassword(MD5Util.generate(password));
            return adminRepository.save(admin);
        } else {
            log.error("修改其他账号密码，无权限。 被修改用户名 = {}， 操作人用户名 = {}",admin.getName(),operator.getName());
            throw new AccessDeniedException("权限不足");
        }

    }

    @Override
    @PreAuthorize("hasAuthority('SALES_CONSULTANT_MANAGEMENT')")
    public Admin salesConsultant(Admin admin) {

        return register(admin, RoleEnum.SALES_CONSULTANT_ADMINISTRATOR.getRoleName());

    }

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
    @PreAuthorize("hasAuthority('PLATFORM_MANAGEMENT')")
    public Admin platformAdministrator(Admin admin) {
        //平台管理员无任何操作权限  只能浏览
        return register(admin, RoleEnum.PLATFORM_ADMINISTRATOR.getRoleName());

    }


    private Admin register(Admin admin, String roleName) {
        Admin rs = new Admin();
        Role role = new Role();
        Optional<Role> roleOptional;
        RoleItem roleItem = new RoleItem();

        Optional<Admin> result = adminRepository.findByUsername(admin.getUsername());
        if (result.isPresent()) {
            log.error("用户名已被注册，username = {}", admin.getUsername());
            throw new CarMallException(CarMallExceptionEnum.USERNAME_IS_EXISTS);
        }

        switch (roleName) {
            case "平台管理员":
                roleOptional = roleRepository.findByName(RoleEnum.PLATFORM_ADMINISTRATOR.getRoleName());
                if (!roleOptional.isPresent()) {
                    role.setName(RoleEnum.PLATFORM_ADMINISTRATOR.getRoleName());
                    role = roleRepository.save(role);
                } else {
                    role = roleOptional.get();
                }
                break;
            case "销售顾问":
                roleOptional = roleRepository.findByName(RoleEnum.SALES_CONSULTANT_ADMINISTRATOR.getRoleName());
                if (!roleOptional.isPresent()) {
                    role.setName(RoleEnum.SALES_CONSULTANT_ADMINISTRATOR.getRoleName());
                    Optional<Privilege> privilege = privilegeRepository.findByPrivilege(PrivilegeAuthorityEnum.CUSTOMER_MANAGEMENT.getIdentifier());
                    PrivilegeItem privilegeItem = new PrivilegeItem();
                    privilegeItem.setPrivilege(privilege.get());
                    role.getPrivilegeItems().add(privilegeItem);
                    role = roleRepository.save(role);
                } else {
                    role = roleOptional.get();
                }
                //绑定所属门店
                Admin principal = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                rs.setStore(principal.getStore());
                break;
            case "门店管理员":
                roleOptional = roleRepository.findByName(RoleEnum.STORE_ADMINISTRATOR.getRoleName());
                if (!roleOptional.isPresent()) {
                    role.setName(RoleEnum.STORE_ADMINISTRATOR.getRoleName());
                    Optional<Privilege> privilege = privilegeRepository.findByPrivilege(PrivilegeAuthorityEnum.SALES_CONSULTANT_MANAGEMENT.getIdentifier());
                    PrivilegeItem privilegeItem1 = new PrivilegeItem();
                    PrivilegeItem privilegeItem2 = new PrivilegeItem();
                    privilegeItem1.setPrivilege(privilege.get());
                    role.getPrivilegeItems().add(privilegeItem1);
                    privilege = privilegeRepository.findByPrivilege(PrivilegeAuthorityEnum.CUSTOMER_MANAGEMENT.getIdentifier());
                    privilegeItem2.setPrivilege(privilege.get());
                    role.getPrivilegeItems().add(privilegeItem2);
                    role = roleRepository.save(role);
                } else {
                    role = roleOptional.get();
                }
                //绑定门店
                rs.setStore(admin.getStore());
                break;
            default:
                break;

        }

        rs.setPhone(admin.getPhone());
        rs.setUsername(admin.getUsername());
        rs.setPassword(admin.getPassword());
        //todo 加密 暂时注释
//        rs.setPassword(MD5Util.generate(admin.getPassword()));
        rs.setName(admin.getName());
        roleItem.setRole(role);
        rs.getRoleItems().add(roleItem);


        return adminRepository.save(rs);

    }

    @Override
    public String init(String curtain) {
        if (curtain.equals("wcp")) {
            Admin admin = new Admin();

            admin.setUsername("test");
            admin.setPassword("1");
            admin.setPhone("13512343422");

            PrivilegeItem privilegeItem1 = new PrivilegeItem();
            PrivilegeItem privilegeItem2 = new PrivilegeItem();
            PrivilegeItem privilegeItem3 = new PrivilegeItem();
            PrivilegeItem privilegeItem4 = new PrivilegeItem();
            PrivilegeItem privilegeItem5 = new PrivilegeItem();

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
            Privilege privilege5 = new Privilege();
            privilege5.setPrivilege(PrivilegeAuthorityEnum.PLATFORM_MANAGEMENT.getIdentifier());
            privilege5.setCategory(5);
            privilege5.setName("平台管理");

            privilege1 = privilegeRepository.save(privilege1);
            privilege2 = privilegeRepository.save(privilege2);
            privilege3 = privilegeRepository.save(privilege3);
            privilege4 = privilegeRepository.save(privilege4);
            privilege5 = privilegeRepository.save(privilege5);

            privilegeItem1.setPrivilege(privilege1);
            privilegeItem2.setPrivilege(privilege2);
            privilegeItem3.setPrivilege(privilege3);
            privilegeItem4.setPrivilege(privilege4);
            privilegeItem5.setPrivilege(privilege5);

            Role role1 = new Role();
            role1.setName("超级管理员");
            role1.getPrivilegeItems().add(privilegeItem1);
            role1.getPrivilegeItems().add(privilegeItem2);
            role1.getPrivilegeItems().add(privilegeItem3);
            role1.getPrivilegeItems().add(privilegeItem4);
            role1.getPrivilegeItems().add(privilegeItem5);

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
//    @PreAuthorize("authenticated && (#user.id==authentication.principal.id || hasAuthority('B1'))")
    public Admin delete(String id) {
        Admin admin = findOne(id);
        //将权限清空
        admin.setRoleItems(null);
        //将用户名无效化
        admin.setUsername("invalidUsername" + admin.getCreatetime());
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
     * 配置了修改权限  只有用户本身能修改 或者 包含(管理这个权限)
     *
     * @param admin
     * @return
     */
    @Override
//    @PreAuthorize("authenticated && (#user.id==authentication.principal.id || hasAuthority('B1'))")
    public Admin update(Admin admin) {
        //todo 只有用户管理员可以修改   或者 自己改自己本身的    password 不能被修改
        //todo  超级管理员admin的权限和角色不允许修改
        Admin result = findOne(admin.getId());
        Set<String> filter = new HashSet<>();
        filter.add("password");
        filter.add("store");
        filter.add("roleItems");
        filter.add("username");
        BeanUtils.copyProperties(admin, result, BeanCopyUtil.filterProperty(admin, filter));

        admin.privilegeCheck();
        return adminRepository.save(result);
    }


}
