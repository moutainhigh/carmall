package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.enums.OrderStatus;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

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
    @Size(min = 11,max = 11,message = "手机号长度不正确")
    private String phone;

    @SchemaDocumentation("用户")
    @ManyToOne
    private Customer customer;

    @SchemaDocumentation("状态:0表示未完成，1表示完成")
    private int status = OrderStatus.NEW.getCode();

    @SchemaDocumentation("门店Id")
    private String storeId;

    @SchemaDocumentation("销售顾问Id")
    private String salesConsultantId;

    @SchemaDocumentation("意向")
    private String intention;

    @SchemaDocumentation("车型")
    private String carModel;

    @SchemaDocumentation("工作")
    private String job;

    @SchemaDocumentation("收入")
    private String income;

    @SchemaDocumentation("婚姻状况")
    private String maritalStatus;

    @SchemaDocumentation("图片信息")
    private String img;
}
