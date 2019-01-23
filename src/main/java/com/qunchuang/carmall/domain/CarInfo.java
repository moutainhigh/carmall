package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.dataprivilege.PrivilegeConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2019/1/15 10:30
 */

@Entity
@Bostype("C02")
@SchemaDocumentation("车辆信息")
@Getter
@Setter
public class CarInfo extends BosEntity{

    @SchemaDocumentation("汽车名称")
    private String name;

    @SchemaDocumentation("品牌")
    private String brand;

    @SchemaDocumentation("型号")
    private String model;

    @SchemaDocumentation("厂商指导价")
    private String price;

    @SchemaDocumentation("权重(越高越前面)")
    private String sort;

    @SchemaDocumentation("图片")
    private String img;

//    @SchemaDocumentation("金融方案")


    @PrivilegeConstraint
    public QueryFilter getPrivilegeConstraint(){
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
//
//        String constraint = "#p.id == 1 ? #builder.start('name = zlm').end() : false";

        //todo  获取princpal 并进行权限判断
       return null;
    }

}
