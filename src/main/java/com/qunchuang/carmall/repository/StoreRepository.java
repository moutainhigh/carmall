package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2019/1/15 14:12
 */
@Repository
public interface StoreRepository extends JpaRepository<Store,String> {
}
