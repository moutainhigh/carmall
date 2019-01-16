package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import graphql.annotation.SchemaDocumentation;
import lombok.Getter;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2019/1/15 10:30
 */

@Entity
@Bostype("C02")
@SchemaDocumentation("车辆信息")
@Getter
public class CarInfo extends BosEntity{

    @SchemaDocumentation("汽车名称")
    private String name;
}
