package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.Consult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Curtain
 * @date 2019/1/21 8:17
 */
@Repository
public interface ConsultRepository extends JpaRepository<Consult,String> {

    /**
     * 通过手机号查询咨询单
     * @param phone
     * @return
     */
    List<Consult> findByPhone(String phone);
}
