package com.moneyapi.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneyapi.exception.TransactionException;
import com.moneyapi.exception.UserException;
import com.moneyapi.model.DataModel;
import com.moneyapi.model.Transaction;
import com.moneyapi.model.TransactionStatus;
import com.moneyapi.service.TransactionService;
import com.moneyapi.service.TransactionService.TransactionId;
import com.moneyapi.service.WithdrawalService;
import com.moneyapi.utils.ValidatorUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private DataModel model;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private ValidatorUtils utils;

	@Autowired
	private Environment env;

	@Autowired
	private TransactionService transactionService;

	private ThreadLocal<Integer> threadLocalVariable = ThreadLocal.withInitial(() -> 0);

	/**
	 * Transfer money from one account to another.
	 * 
	 * @param senderId   The unique identifier of the sender's account.
	 * @param receiverId The unique identifier of the receiver's account.
	 * @param amount     The amount of money to be transferred.
	 * @return A ResponseEntity containing a response message indicating the success
	 *         or failure of the money transfer.
	 */
	@GetMapping("/transferMoney/{senderId}/{receiverId}/{amount}")
	public ResponseEntity<?> transferMoney(@PathVariable Integer senderId, @PathVariable String receiverId,
			@PathVariable BigDecimal amount) {

		// This method transfers 'amount' from 'senderId' to 'receiverId'.
		// Returns a response message indicating the result of the transfer.

		try {

			// validate all input parameters
			utils.validate(senderId, receiverId, amount);

			// runs all requests concurrently
			Runnable task = () -> {

				threadLocalVariable.set(new TransactionId(model.getTransactionId().getAndIncrement()).id());

				model.getTransactions().put(threadLocalVariable.get(),
						new Transaction(threadLocalVariable.get(), senderId, receiverId, amount, LocalDateTime.now(),
								TransactionStatus.PROCESSING, env.getProperty("MESSAGE_011")));

				var message = transactionService.transferMoney(threadLocalVariable.get(), senderId, receiverId, amount);
				log.info("{}", message);

				threadLocalVariable.remove();
			};

			taskExecutor.execute(task);

			return ResponseEntity.accepted().body("Request accepted for processing");

		} catch (TransactionException e) {

			log.error("Transaction Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	/**
	 * Retrieves the status of a transaction based on its unique transaction ID.
	 *
	 * @param transactionId The unique identifier for the transaction. Required.
	 * @return A ResponseEntity containing the status of the transaction. If the
	 *         transaction is found, it returns HTTP status 200 (OK) with the
	 *         transaction status. If the transaction is not found or an error
	 *         occurs, it returns HTTP status 500 (Internal Server Error) with an
	 *         error message.
	 */
	@GetMapping("/transactionStatus/{transactionId}")
	public ResponseEntity<?> getTransactionStatus(@PathVariable Integer transactionId) {

		try {
			var transaction = transactionService.getTransactionStatus(transactionId);
			log.info("{}", transaction);
			return ResponseEntity.ok(transaction);
		} catch (UserException e) {
			log.error("Transaction Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

	}

	/**
	 * Retrieves the transaction history.
	 *
	 * @return A list of transactions representing the transaction history.
	 */
	@GetMapping("/transactionHistory")
	public ResponseEntity<?> getTransactionHistory() {

		// Create a new list containing all transactions from the model

		return ResponseEntity.ok(new ArrayList<>(model.getTransactions().values()));
	}

	/**
	 * Retrieves a list of transactions associated with a specific user.
	 *
	 * @param userId The unique identifier of the user whose transactions are
	 *               requested.
	 * @return A ResponseEntity containing the status of the transaction. If the
	 *         transaction is found, it returns HTTP status 200 (OK) with the
	 *         transaction status. If the transaction is not found or an error
	 *         occurs, it returns HTTP status 500 (Internal Server Error) with an
	 *         error message.
	 */
	@GetMapping("/getTransactionByUser/{userId}")
	public ResponseEntity<?> getTransactionByUser(@PathVariable Integer userId) {

		// Retrieve all transactions and filter those where the sender's ID matches the
		// provided userId.
		return ResponseEntity
				.ok(model.getTransactions().values().stream().filter(e -> e.getSenderId() == userId).toList());
	}

	/**
	 * Transfers funds externally from the sender's account to the specified
	 * external address.
	 *
	 * @param senderId The unique identifier of the sender's account.
	 * @param address  The external address to which the funds will be transferred.
	 * @param amount   The amount of money to be transferred.
	 * @return A response indicating the success or failure of the external
	 *         withdrawal.
	 */

	@GetMapping("/transferExternalAddress/{senderId}/{address}/{amount}")
	public ResponseEntity<?> transferExternalAddress(@PathVariable Integer senderId,
			@PathVariable WithdrawalService.Address address, @PathVariable BigDecimal amount) {

		// This method transfers 'amount' from 'senderId' to 'receiverId'.
		// Returns a response message indicating the result of the transfer.

		try {

			// validate all input parameters
			utils.validate(senderId, address, amount);

			// runs all requests concurrently
			Runnable task = () -> {

				threadLocalVariable.set(new TransactionId(model.getTransactionId().getAndIncrement()).id());

				/*
				 * For transaction status, we will check after 20 sec. We will add those
				 * transactions in queue who have PROCESSING status.
				 */
				model.getTransactions().put(threadLocalVariable.get(),
						new Transaction(threadLocalVariable.get(), senderId, address.value(), amount,
								LocalDateTime.now().plusSeconds(20), TransactionStatus.PROCESSING,
								env.getProperty("MESSAGE_011")));

				var message = transactionService.transferExternalAddress(threadLocalVariable.get(), senderId, address,
						amount);
				log.info("{}", message);

			};

			taskExecutor.execute(task);

			return ResponseEntity.accepted().body("Request accepted for processing");

		} catch (TransactionException e) {

			log.error("Transaction Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

	}

	public void checkTransactionStatus() {
		taskExecutor.execute(() -> {
			while(true) {
				if (!model.getTransactionWithdrawalQueue().isEmpty()) {
					
					transactionService.checkTransactionStatus(model.getTransactionWithdrawalQueue().peek());
				}
			}
			
		});

	}

}
