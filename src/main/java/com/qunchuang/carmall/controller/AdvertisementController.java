package com.qunchuang.carmall.controller;

import com.qunchuang.carmall.domain.Advertisement;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Curtain
 * @date 2019/2/19 9:26
 */
@GraphqlController("advertisement")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @SchemaDocumentation("增加广告信息")
    @GraphqlMutation(path = "/add")
    public Advertisement add(Advertisement advertisement) {
        return advertisementService.add(advertisement);
    }

    @SchemaDocumentation("修改广告")
    @GraphqlMutation(path = "/modify")
    public Advertisement modify(Advertisement advertisement) {
        return advertisementService.modify(advertisement);
    }

    @SchemaDocumentation("删除广告")
    @GraphqlMutation(path = "/delete")
    public Advertisement delete(String id) {
        return advertisementService.delete(id);
    }

}
