package com.qunchuang.carmall;

import cn.wzvtcsoft.bosdomain.persist.BosJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(repositoryBaseClass = BosJpaRepositoryImpl.class)
public class CarMallApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarMallApplication.class, args);
	}

}

