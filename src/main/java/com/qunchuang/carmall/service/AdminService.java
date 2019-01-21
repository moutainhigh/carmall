package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Admin;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Curtain
 * @date 2019/1/16 9:38
 */
public interface AdminService extends UserDetailsService{
    /**
     * 管理员添加用户
     * @param admin
     * @return
     */
    Admin save(Admin admin);

    /**
     * 普通用户注册
     *
     * @param admin
     * @return
     */
    Admin register(Admin admin);

    /**
     * 修改
     * @param admin
     * @return
     */
    Admin update(Admin admin);

    /**
     * 通过id查询
     * @param id
     * @return
     */
    Admin findOne(String id);

    /**
     * 删除管理员
     * @param id
     * @return
     */
    Admin delete(String id);

    /**
     * 初始化管理员账号
     * @param curtain
     * @return
     */
    String init(String curtain);
}
