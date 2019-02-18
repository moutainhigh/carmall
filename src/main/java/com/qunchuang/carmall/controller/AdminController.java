package com.qunchuang.carmall.controller;


import cn.wzvtcsoft.validator.anntations.DomainRule;
import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.service.AdminService;
import cn.wzvtcsoft.validator.anntations.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2019/1/16 10:22
 */
@GraphqlController("admin")
@MutationValidated
public class AdminController {
    @Autowired
    private AdminService adminService;

    @SchemaDocumentation("添加平台管理员")
    @GraphqlMutation(path = "/platformAdministrator")
    public Admin platformAdministrator(@DomainRule("phone && password && username") Admin admin) {
        return adminService.platformAdministrator(admin);
    }

    @SchemaDocumentation("添加销售顾问")
    @GraphqlMutation(path = "/salesConsultant")
    public Admin salesConsultant(@DomainRule("phone && password && username") Admin admin) {
        return adminService.salesConsultant(admin);
    }

    @SchemaDocumentation("添加门店管理员")
    @GraphqlMutation(path = "/storeAdministrator")
    public Admin storeAdministrator(@DomainRule("phone && password && username") Admin admin) {
        return adminService.storeAdministrator(admin);
    }

    @SchemaDocumentation("修改管理员")
    @GraphqlMutation(path = "/modify")
    public Admin modify(Admin admin) {
        return adminService.update(admin);
    }

    @SchemaDocumentation("删除管理员")
    @GraphqlMutation(path = "/delete")
    public Admin delete(String id) {
        return adminService.delete(id);
    }


}
