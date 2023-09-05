package com.moneyapi.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class DataModel {

	private ConcurrentHashMap<Integer, User> users;
	private ConcurrentHashMap<Integer, Transaction> transactions;
	private ConcurrentHashMap<Integer, TransactionStatus> orderStatus;
	private AtomicInteger transactionId;
	private AtomicInteger userId;
	private final BlockingQueue<Integer> transactionWithdrawalQueue;

	public DataModel() {
		users = new ConcurrentHashMap<>(10);
		transactions = new ConcurrentHashMap<>();
		orderStatus = new ConcurrentHashMap<>();
		transactionId = new AtomicInteger(1);
		userId = new AtomicInteger(1);
		transactionWithdrawalQueue = new LinkedBlockingQueue<>();
	}

}
