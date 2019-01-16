package com.qunchuang.carmall.domain.service;

import com.qunchuang.carmall.domain.privilege.Role;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/16 8:23
 */

public class RoleServiceImpl {
    @Autowired
    private RoleRepository roleRepository;

    @PreAuthorize("hasAuthority('C1')")
    public Role create(Role role) {
        //todo 需要校验constraint 是否符合规则 能否转化成qfilter  失败则抛出异常
        //校验角色名是否已经存在
        Optional<Role> result = roleRepository.findByName(role.getName());
        if (result.isPresent()) {
            throw new CarMallException(CarMallExceptionEnum.ROLE_IS_EXISTS);
        }
        role.privilegeCheck();
        role = roleRepository.save(role);

        return role;
    }

    @PreAuthorize("hasAuthority('C1')")
    public Role modify(Role role) {
        //todo 需要校验constraint 是否符合规则 能否转化成qfilter  失败则抛出异常
        role.privilegeCheck();
        role = roleRepository.save(role);

        return role;
    }
}
