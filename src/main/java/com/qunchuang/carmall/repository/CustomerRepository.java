package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2019/1/14 9:00
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer,String>{
    Optional<Customer> findByOpenid(String openid);

    Optional<Customer> findByPhone(String phone);
}
