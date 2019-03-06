package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.IntegralRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2019/3/6 9:50
 */
@Repository
public interface IntegralRecordRepository extends JpaRepository<IntegralRecord,String> {
}
