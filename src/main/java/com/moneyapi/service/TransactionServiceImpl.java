package com.moneyapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.moneyapi.exception.TransactionException;
import com.moneyapi.model.DataModel;
import com.moneyapi.model.Transaction;
import com.moneyapi.model.TransactionStatus;
import com.moneyapi.service.WithdrawalService.Address;
import com.moneyapi.service.WithdrawalService.WithdrawalState;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private DataModel model;

	@Autowired
	private WithdrawalService withdrawalService;

	@Autowired
	private Environment env;

	@Autowired
	private TaskExecutor taskExecutor;

	private final Object lock = new Object();

	@Override
	public String transferMoney(Integer trascantionId, Integer senderId, String receiverId, BigDecimal amount) {

		StringBuilder message = new StringBuilder();

		model.getUsers().get(senderId).setAmount(model.getUsers().get(senderId).getAmount().subtract(amount));
		model.getUsers().get(Integer.valueOf(receiverId))
				.setAmount(model.getUsers().get(Integer.valueOf(receiverId)).getAmount().add(amount));

		model.getTransactions().get(trascantionId).setStatus(TransactionStatus.COMPLETED);
		model.getTransactions().get(trascantionId).setDescription(env.getProperty("MESSAGE_007"));

		message.append(trascantionId).append(" ").append(env.getProperty("MESSAGE_007"));

		return message.toString();
	}

	@Override
	public Transaction getTransactionStatus(Integer transactionId) {

		if (model.getTransactions().containsKey(transactionId)) {
			return model.getTransactions().get(transactionId);
		} else {
			throw new TransactionException(new StringBuilder().append(transactionId).append(" ")
					.append(env.getProperty("MESSAGE_009")).toString());
		}

	}

	@Override
	public String transferExternalAddress(Integer trascantionId, Integer senderId, Address address, BigDecimal amount) {

		StringBuilder message = new StringBuilder();

		model.getUsers().get(senderId).setAmount(model.getUsers().get(senderId).getAmount().subtract(amount));

		// External address logic.
		WithdrawalService.WithdrawalId id = new WithdrawalService.WithdrawalId(UUID.randomUUID());
		withdrawalService.requestWithdrawal(id, address, amount);

		WithdrawalState status = withdrawalService.getRequestState(id);

		if (status.compareTo(WithdrawalState.COMPLETED) == 0 || status.compareTo(WithdrawalState.PROCESSING) == 0) {

			TransactionStatus transactionStatus = status.compareTo(WithdrawalState.COMPLETED) == 0
					? TransactionStatus.COMPLETED
					: TransactionStatus.PROCESSING;

			model.getTransactions().get(trascantionId).setStatus(transactionStatus);

			model.getTransactions().get(trascantionId)
					.setDescription(new StringBuilder().append(env.getProperty("MESSAGE_012")).append(" ")
							.append(transactionStatus.toString()).toString());

			message.append(trascantionId).append(" ").append(env.getProperty("MESSAGE_007"));

			// if transaction Status is processing then we will add transaction in queue and
			// every after 20ms we will check the status.
			if (transactionStatus.compareTo(TransactionStatus.PROCESSING) == 0) {
				log.info("{} is added in the queue. Status : {}", trascantionId, transactionStatus);
				model.getTransactionWithdrawalQueue().add(trascantionId);
			}

		} else if (status.compareTo(WithdrawalState.FAILED) == 0) {
			model.getTransactions().get(trascantionId).setStatus(TransactionStatus.FAILED);
			model.getTransactions().get(trascantionId)
					.setDescription(env.getProperty("MESSAGE_012") + TransactionStatus.FAILED.toString());

			// reverse money to the sender's account
			model.getUsers().get(senderId).setAmount(model.getUsers().get(senderId).getAmount().add(amount));

		}

		return message.toString();
	}

	public void checkTransactionStatus(Integer transactionId) {

		if (LocalDateTime.now().isAfter(model.getTransactions().get(transactionId).getTimestamp())) {
			log.info("Checking current status of transaction.");
			TransactionStatus status = ThreadLocalRandom.current().nextBoolean() ? TransactionStatus.COMPLETED
					: TransactionStatus.FAILED;
			model.getTransactions().get(transactionId).setStatus(status);
			model.getTransactions().get(transactionId).setDescription(
					model.getTransactions().get(transactionId).getDescription() + " | Transaction is " + status);
			model.getTransactions().get(transactionId).setTimestamp(LocalDateTime.now());
			log.info("{} transaction is {}", model.getTransactionWithdrawalQueue().poll(), status);

			if (status.compareTo(TransactionStatus.FAILED) == 0) {
				Integer senderId = model.getTransactions().get(transactionId).getSenderId();
				BigDecimal amount = model.getTransactions().get(transactionId).getAmount();
				// reverse money to the sender's account
				model.getUsers().get(senderId).setAmount(model.getUsers().get(senderId).getAmount().add(amount));
			}

		}

	}
}
