package com.qunchuang.carmall.domain.privilege;


import cn.wzvtcsoft.bosdomain.Entry;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Curtain
 * @date 2018/10/22 16:54
 */
@Entity
@SchemaDocumentation("角色集合")
@Bostype("A10")
@Getter
@Setter
public class RoleItem extends Entry {
    /**
     * 角色
     */
    @ManyToOne
    private Role role;

    @Override
    public String toString() {
        return "RoleItem{" +
                "role=" + role +
                '}';
    }
}
