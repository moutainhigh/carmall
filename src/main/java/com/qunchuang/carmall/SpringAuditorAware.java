package com.qunchuang.carmall;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/11/20 10:42
 */

@Component("auditorAware")
public class SpringAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return Optional.of("111111111");
    }}

