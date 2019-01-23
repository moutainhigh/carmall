package com.qunchuang.carmall.controller;


import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.service.AdminService;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2019/1/16 10:22
 */
@GraphqlController("admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @SchemaDocumentation("添加管理员")
    @GraphqlMutation(path = "/add")
    public Admin add(Admin admin){
        return adminService.save(admin);
    }

    @SchemaDocumentation("修改管理员")
    @GraphqlMutation(path = "/modify")
    public Admin modify(Admin admin){
        return adminService.update(admin);
    }

    @SchemaDocumentation("删除管理员")
    @GraphqlMutation(path = "/delete")
    public Admin delete(String id){
        return adminService.delete(id);
    }


}
