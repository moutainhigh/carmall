package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/15 14:10
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin,String> {
    /**
     * 通过用户名查找用户
     * @param username
     * @return
     */
    Optional<Admin> findByUsername(String username);



}
