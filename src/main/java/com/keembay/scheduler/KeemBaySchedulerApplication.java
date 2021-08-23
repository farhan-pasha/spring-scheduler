package com.keembay.scheduler;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.keembay.scheduler"})
@EnableBatchProcessing
@EnableTransactionManagement
public class KeemBaySchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeemBaySchedulerApplication.class, args);
	}

}



