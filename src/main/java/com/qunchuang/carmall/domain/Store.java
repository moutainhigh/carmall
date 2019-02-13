package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * @author Curtain
 * @date 2019/1/15 10:43
 */
@Entity
@Bostype("S01")
@SchemaDocumentation("门店")
@Getter
@Setter
@ToString
public class Store extends BosEntity {

    @SchemaDocumentation("门店名称")
    private String name;

    @SchemaDocumentation("地址")
    private String address;

    @SchemaDocumentation("门店负责人")
    private String people;

    @SchemaDocumentation("联系方式")
    @Size(min = 11,max = 11,message = "手机号长度不正确")
    private String phone;

    @SchemaDocumentation("门店图片")
    private String img;

    @SchemaDocumentation("经度")
    private String longitude;

    @SchemaDocumentation("纬度")
    private String latitude;


}
