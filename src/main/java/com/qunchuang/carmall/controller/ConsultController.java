package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Consult;
import com.qunchuang.carmall.service.ConsultService;
import graphql.annotation.GraphqlController;
import graphql.annotation.GraphqlMutation;
import graphql.annotation.SchemaDocumentation;
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
    @GraphqlMutation(path = "/consult")
    public Consult consult(Consult consult){
        //todo 用户咨询时 还没有注册 。。
        return consultService.add(consult);
    }

    @SchemaDocumentation("派单")
    public Consult allocate(String id,String salesId){
        return consultService.allocate(id,salesId);
    }

    @SchemaDocumentation("修改")
    public Consult modify(Consult consult){
        return consultService.modify(consult);
    }
}
