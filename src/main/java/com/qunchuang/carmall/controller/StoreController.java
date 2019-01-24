package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Store;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2019/1/16 10:22
 */
@GraphqlController("store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @SchemaDocumentation("添加门店")
    @GraphqlMutation(path = "/add")
    public Store add(Store store){
        return storeService.add(store);
    }

    @SchemaDocumentation("修改门店")
    @GraphqlMutation(path = "/modify")
    public Store modify(Store store){
        return storeService.modify(store);
    }

    @SchemaDocumentation("删除门店")
    @GraphqlMutation(path = "/delete")
    public Store delete(String id){
        return storeService.delete(id);
    }

}
