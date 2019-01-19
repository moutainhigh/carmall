package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2019/1/19 15:14
 */

@Entity
@Bostype("C03")
@SchemaDocumentation("咨询")
@Getter
@Setter
public class Consult extends BosEntity{

    @SchemaDocumentation("名称")
    private String name;

    @SchemaDocumentation("手机号")
    private String phone;

    @SchemaDocumentation("备注")
    private String remark;

    @SchemaDocumentation("门店Id")
    private String storeId;

    @SchemaDocumentation("销售顾问Id")
    private String salesConsultantId;


}
