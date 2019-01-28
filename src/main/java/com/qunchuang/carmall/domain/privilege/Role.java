package com.qunchuang.carmall.domain.privilege;


import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.domain.Admin;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Curtain
 * @date 2018/10/9 14:15
 */

@Entity
@SchemaDocumentation("角色")
@Bostype("R01")
@Getter
@Setter
public class Role extends BosEntity {

    /**
     * 角色
     */
    @SchemaDocumentation("角色名")
    private String name;

    /**
     * 权限集合
     */
    @SchemaDocumentation("权限集合")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PrivilegeItem> privilegeItems = new HashSet<>();

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", privilegeItems=" + privilegeItems +
                '}';
    }

    /**
     * 验证修改的权限 是否被允许（包含在当前用户中）
     */
    public void privilegeCheck() {
        Admin principal = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Privilege> principalPrivilege = principal.getRoleItems()
                .stream()
                .flatMap(roleItem -> roleItem.getRole().getPrivilegeItems().stream())
                .map(PrivilegeItem::getPrivilege).collect(Collectors.toSet());

        Set<Privilege> rolePrivilege = this.getPrivilegeItems().stream()
                .map(PrivilegeItem::getPrivilege).collect(Collectors.toSet());

        if (!principalPrivilege.containsAll(rolePrivilege)) {
            throw new AccessDeniedException("角色添加错误，赋予了本身不具备的权限");
        }
    }
}
