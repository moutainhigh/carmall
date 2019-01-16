package com.qunchuang.carmall.domain.privilege;


import cn.wzvtcsoft.bosdomain.Entry;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Curtain
 * @date 2018/10/9 14:50
 */

@Entity
@SchemaDocumentation("权限集合")
@Bostype("P02")
@Getter
@Setter
@ToString
public class PrivilegeItem extends Entry {
    /**
     * 权限
     */
    @ManyToOne
    private Privilege privilege;
    /**
     * 约束规则
     */
    private String constraintRule;

}
