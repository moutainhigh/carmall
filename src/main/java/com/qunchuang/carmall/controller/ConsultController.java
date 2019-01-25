package com.qunchuang.carmall.controller;

import cn.wzvtcsoft.validator.anntations.DomainRule;
import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.service.ConsultService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2019/1/21 9:33
 */
@GraphqlController("consult")
public class ConsultController {
    
    @Autowired
    private ConsultService consultService;

    @SchemaDocumentation("发起咨询")
    @GraphqlMutation(path = "/add")
    public Consult add(@DomainRule("phone")Consult consult){
        return consultService.add(consult);
    }

    @SchemaDocumentation("派咨询单")
    @GraphqlMutation(path = "/allocate")
    public Consult allocate(String id,String salesId){
        return consultService.allocate(id,salesId);
    }

    @SchemaDocumentation("修改咨询单信息")
    @GraphqlMutation(path = "/modify")
    public Consult modify(Consult consult){
        return consultService.modify(consult);
    }

    @SchemaDocumentation("完结咨询单")
    @GraphqlMutation(path = "/finish")
    public Consult finish(String id){
        return consultService.finish(id);
    }

    @SchemaDocumentation("转咨询单到门店")
    @GraphqlMutation(path = "/changeToStore")
    public Consult changeToStore(String id,String storeId){
        return consultService.changeToStore(id,storeId);
    }

    @SchemaDocumentation("转咨询单到销售顾问")
    @GraphqlMutation(path = "/changeToSalesConsultant")
    public Consult changeToSalesConsultant(String id,String salesId){
        return consultService.changeToSalesConsultant(id,salesId);
    }

    @SchemaDocumentation("删除咨询单")
    @GraphqlMutation(path = "/delete")
    public Consult delete(String id){
        return consultService.delete(id);
    }
}
