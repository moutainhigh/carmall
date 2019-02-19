package com.qunchuang.carmall.domain;


import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2018/3/12 15:21
 */

@Entity
@Bostype("A02")
@SchemaDocumentation("广告")
@Getter
@Setter
public class Advertisement extends BosEntity {

    @SchemaDocumentation("所属类别")
    private Integer type;

    @SchemaDocumentation("图片路径")
    @Column(length = 1000)
    private String image;

    @SchemaDocumentation("轮播时间/秒")
    private Integer time;

    @SchemaDocumentation("广告跳转网址")
    @Column(length = 1000)
    private String webAddress;


}


