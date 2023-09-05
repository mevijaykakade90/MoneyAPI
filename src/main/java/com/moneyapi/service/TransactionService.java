package com.moneyapi.service;

import java.math.BigDecimal;

import com.moneyapi.model.Transaction;
import com.moneyapi.service.WithdrawalService.Address;

public interface TransactionService {

	record TransactionId(Integer id) {
	}

	String transferMoney(Integer trascantionId, Integer senderId, String receiverId, BigDecimal amount);

	Transaction getTransactionStatus(Integer transactionId);

	String transferExternalAddress(Integer trascantionId,Integer senderId, Address address, BigDecimal amount);

	void checkTransactionStatus(Integer trascantionId);

}
