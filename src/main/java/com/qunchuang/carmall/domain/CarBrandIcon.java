package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2019/1/30 9:30
 */

@Entity
@SchemaDocumentation("品牌优选")
@Bostype("C04")
@Getter
@Setter
public class CarBrandIcon extends BosEntity {

    @SchemaDocumentation("品牌名称")
    private String brand;

    @SchemaDocumentation("图标")
    private String icon;

    @SchemaDocumentation("是否已选")
    private boolean choose = false;
}
