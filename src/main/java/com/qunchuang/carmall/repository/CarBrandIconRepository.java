package com.qunchuang.carmall.repository;

import com.qunchuang.carmall.domain.CarBrandIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/1/39 9:57
 */
@Repository
public interface CarBrandIconRepository extends JpaRepository<CarBrandIcon,String> {

    Optional<CarBrandIcon> findByBrand(String brand);
}
