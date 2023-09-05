package com.moneyapi.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.moneyapi.controller.TransactionController;

@SpringBootApplication(scanBasePackages = { "com.moneyapi" })
public class MoneyApiApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(MoneyApiApplication.class, args);
		TransactionController transactionController = context.getBean(TransactionController.class);
		
		transactionController.checkTransactionStatus();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10); // Number of threads to keep in the pool
		executor.setMaxPoolSize(20); // Maximum number of threads
		executor.setQueueCapacity(30); // Size of the queue for incoming tasks
		executor.initialize();
		return executor;
	}

}
