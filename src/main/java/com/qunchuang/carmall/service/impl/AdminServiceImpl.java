package com.qunchuang.carmall.service.impl;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.AdminRepository;
import com.qunchuang.carmall.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("authenticated && (#user.id==authentication.principal.id || hasAuthority('B1'))")
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
