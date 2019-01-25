package com.qunchuang.carmall.service;

import com.qunchuang.carmall.domain.Admin;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Curtain
 * @date 2019/1/16 9:38
 */
public interface AdminService extends UserDetailsService{
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

    /**
     * 创建平台管理员
     * @param admin
     * @return
     */
    Admin platformAdministrator(Admin admin);

    /**
     * 创建门店管理员
     * @param admin
     * @return
     */
    Admin storeAdministrator(Admin admin);

    /**
     * 创建销售人员
     * @param admin
     * @return
     */
    Admin salesConsultant(Admin admin);

    /**
     * 通过id判断是否存在
     * @param id
     * @return
     */
    void existsById(String id);
}
