package com.qunchuang.carmall.repository;


import com.qunchuang.carmall.domain.privilege.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/10/9 15:38
 */
public interface PrivilegeRepository extends JpaRepository<Privilege,String> {
    /**
     * 通过名称查询
     * @param name
     * @return
     */
    Optional<Privilege> findByName(String name);
}
