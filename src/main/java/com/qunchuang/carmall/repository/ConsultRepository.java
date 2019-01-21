package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.Consult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2019/1/21 8:17
 */
@Repository
public interface ConsultRepository extends JpaRepository<Consult,String> {
}
