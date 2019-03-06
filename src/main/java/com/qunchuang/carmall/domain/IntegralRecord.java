package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Curtain
 * @date 2019/3/6 9:29
 */
@Entity
@Bostype("I01")
@SchemaDocumentation("积分记录")
@Getter
@Setter
public class IntegralRecord extends BosEntity {

    @SchemaDocumentation("类型：1增加 2扣除")
    private Integer category;

    @SchemaDocumentation("本次积分增加或扣除")
    private Integer  integralCurrent;

    @SchemaDocumentation("积分余额")
    private Integer  integralBalance;

    @SchemaDocumentation("咨询单")
    @ManyToOne
    private Consult consult;

    @SchemaDocumentation("用户")
    @ManyToOne
    private Customer customer;

    public IntegralRecord(){}

    public IntegralRecord(Integer category, Integer integralCurrent, Integer integralBalance, Consult consult, Customer customer) {
        this.category = category;
        this.integralCurrent = integralCurrent;
        this.integralBalance = integralBalance;
        this.consult = consult;
        this.customer = customer;
    }
}
