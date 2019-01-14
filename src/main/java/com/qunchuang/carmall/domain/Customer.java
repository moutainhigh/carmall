package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.exception.CarMallException;
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


    public void setOpenid(String openid) {
        if (openid!=null){
            this.openid = openid;
        }
        throw new CarMallException("openid不能为空");
    }
}
