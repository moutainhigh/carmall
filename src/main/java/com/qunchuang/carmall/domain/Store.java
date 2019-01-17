package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Curtain
 * @date 2019/1/15 10:43
 */
@Entity
@Bostype("S01")
@SchemaDocumentation("门店")
@Getter
@Setter
public class Store extends BosEntity {

    @SchemaDocumentation("门店名称")
    private String name;

    @SchemaDocumentation("地址")
    private String address;

    @SchemaDocumentation("门店负责人")
    private String people;

    @SchemaDocumentation("联系方式")
    private String phone;

    @SchemaDocumentation("销售顾问")
    @OneToMany
    private Set<Admin> admins = new HashSet<>();


}
