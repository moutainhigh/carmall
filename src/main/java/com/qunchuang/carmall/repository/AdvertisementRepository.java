package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/3/14 10:47
 */

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement,String> {

    Optional<Advertisement> findByType(Integer type);
}
