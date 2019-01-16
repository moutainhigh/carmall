package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import graphql.annotation.SchemaDocumentation;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.ValidationException;

/**
 * @author Curtain
 * @date 2019/1/14 8:48
 */
@Entity
@Bostype("C01")
@SchemaDocumentation("顾客")
@Getter
public class Customer extends BosEntity{

    @SchemaDocumentation("昵称")
    private String nickname;

    @SchemaDocumentation("微信openid")
    private String openid;

    @SchemaDocumentation("性别")
    @Column(length = 10)
    private String gender;

    @SchemaDocumentation("名称")
    private String name;

    @SchemaDocumentation("手机号")
    private String phone;

    @SchemaDocumentation("所属销售顾问")
    private String salesConsultantId;

    @SchemaDocumentation("积分")
    private int integral;

    @SchemaDocumentation("是否是销售顾问")
    private boolean salesConsultant;

    public void setOpenid(String openid) {
        if (openid==null){
          throw new CarMallException(CarMallExceptionEnum.USER_ARGS_NOT_TRUE);

        }
        this.openid = openid;
    }
}
