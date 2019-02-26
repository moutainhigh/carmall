package com.qunchuang.carmall.auth.app;

import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取管理员信息
 *
 * @author Curtain
 * @date 2019/2/26 14:23
 */
@Component
public class AppAdminInfo {

    @Autowired
    private AdminService adminService;

    public Admin getAdmin(String username) {

        Admin admin = adminService.findByUsername(username);

        return admin;
    }
}
