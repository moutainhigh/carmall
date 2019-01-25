package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.domain.privilege.Privilege;
import com.qunchuang.carmall.domain.privilege.PrivilegeItem;
import com.qunchuang.carmall.domain.privilege.RoleItem;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Curtain
 * @date 2019/1/15 10:32
 */

@Entity
@Bostype("A01")
@SchemaDocumentation("管理员")
@Getter
@Setter
public class Admin extends BosEntity implements UserDetails{

    @SchemaDocumentation("姓名")
    private String name;

    @SchemaDocumentation("用户名")
    private String username;

    @SchemaDocumentation("密码")
    private String password;

    @SchemaDocumentation("手机号")
    @Size(min = 11,max = 11,message = "手机号长度不正确")
    private String phone;

//    @SchemaDocumentation("是否是销售人员")
//    private boolean salesConsultant;
//
//    @SchemaDocumentation("是否是门店")
//    private boolean store;

    @SchemaDocumentation("门店id")
    private String storeId;
    //TODO 销售员 一定是属于门店的吗

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<RoleItem> roleItems = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> collect = roleItems
                .stream()
                .map(RoleItem::getRole)
                .flatMap(role -> role.getPrivilegeItems().stream())
                .map(PrivilegeItem::getPrivilege)
                .map(privilege -> new SimpleGrantedAuthority(privilege.getAuthority()))
                .collect(Collectors.toSet());

        return collect;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 验证修改的权限 是否被允许（包含在当前用户中)
     */
    public void privilegeCheck() {
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Privilege> principalPrivilege = admin.getRoleItems()
                .stream()
                .flatMap(roleItem -> roleItem.getRole().getPrivilegeItems().stream())
                .map(PrivilegeItem::getPrivilege).collect(Collectors.toSet());

        Set<Privilege> administerPrivilege = admin.getRoleItems()
                .stream()
                .flatMap(roleItem -> roleItem.getRole().getPrivilegeItems().stream())
                .map(PrivilegeItem::getPrivilege).collect(Collectors.toSet());

        if (!principalPrivilege.containsAll(administerPrivilege)) {
            throw new AccessDeniedException("权限添加错误，赋予了本身不具备的权限");
        }
    }

}
