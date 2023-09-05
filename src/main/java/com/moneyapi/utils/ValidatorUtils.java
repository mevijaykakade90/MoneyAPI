package com.moneyapi.utils;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.moneyapi.exception.TransactionException;
import com.moneyapi.exception.UserException;
import com.moneyapi.model.DataModel;
import com.moneyapi.model.Transaction;
import com.moneyapi.model.TransactionStatus;
import com.moneyapi.service.WithdrawalService.Address;

@Service
public class ValidatorUtils {

	@Autowired
	private DataModel model;

	@Autowired
	private Environment env;

	public void validate(Integer senderId, String receiverId, BigDecimal amount) throws TransactionException {

		StringBuilder message = new StringBuilder();

		if (senderId <= 0 || !model.getUsers().containsKey(senderId)) {
			throw new TransactionException(
					message.append(senderId).append(" ").append(env.getProperty("MESSAGE_003")).toString());
		}
		if (receiverId.isBlank() || receiverId.isEmpty()) {

			throw new TransactionException(
					message.append(receiverId).append(" ").append(env.getProperty("MESSAGE_004")).toString());

		}
		if (!receiverId.isBlank() || !receiverId.isEmpty()) {

			try {
				int id = Integer.parseInt(receiverId);
				if (!model.getUsers().containsKey(id)) {
					throw new TransactionException(
							message.append(receiverId).append(" ").append(env.getProperty("MESSAGE_004")).toString());
				}
			} catch (NumberFormatException e) {
				throw new TransactionException(
						message.append(receiverId).append(" ").append(env.getProperty("MESSAGE_004")).toString());
			}

		}
		if (amount.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new TransactionException(
					message.append(amount).append(" ").append(env.getProperty("MESSAGE_005")).toString());
		}
		if (model.getUsers().get(senderId).getAmount().compareTo(amount) < 0) {
			throw new TransactionException(message.append(model.getUsers().get(senderId).getName()).append(" ")
					.append(env.getProperty("MESSAGE_006")).toString());
		}

	}

	public void validate(Integer senderId, Address address, BigDecimal amount) throws TransactionException {

		StringBuilder message = new StringBuilder();

		if (senderId <= 0 || !model.getUsers().containsKey(senderId)) {
			throw new TransactionException(
					message.append(senderId).append(" ").append(env.getProperty("MESSAGE_003")).toString());
		}

		if (address.value().isBlank() || address.value().isEmpty()) {
			throw new TransactionException(
					message.append(address.value()).append(" ").append(env.getProperty("MESSAGE_010")).toString());
		}

		if (amount.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new TransactionException(
					message.append(amount).append(" ").append(env.getProperty("MESSAGE_005")).toString());
		}

		if (model.getUsers().get(senderId).getAmount().compareTo(amount) < 0) {
			throw new TransactionException(message.append(model.getUsers().get(senderId).getName()).append(" ")
					.append(env.getProperty("MESSAGE_006")).toString());
		}

		Optional<Entry<Integer, Transaction>> transaction = model.getTransactions().entrySet().parallelStream()
				.filter(e -> e.getValue().getReceiverId().equals(address.value())
						&& e.getValue().getAmount().compareTo(amount) == 0
						&& e.getValue().getStatus().compareTo(TransactionStatus.PROCESSING) == 0)
				.findFirst();

		if (transaction.isPresent()) {
			throw new TransactionException(message.append("Transaction request with id ")
					.append(transaction.get().getKey()).append(" is already present.").toString());
		}

	}

	public void validate(String name) throws UserException {
		if (model.getUsers().values().stream().anyMatch(e -> e.getName().equalsIgnoreCase(name))) {
			throw new UserException(
					new StringBuilder().append(name).append(" ").append(env.getProperty("MESSAGE_001")).toString());
		}
	}

	public void validate(Integer userId, BigDecimal amount) throws UserException {
		if (!model.getUsers().containsKey(userId)) {
			throw new UserException(
					new StringBuilder().append(userId).append(" ").append(env.getProperty("MESSAGE_002")).toString());
		}

		if (amount.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new TransactionException(amount + " " + env.getProperty("MESSAGE_005"));
		}

	}

	public void validate(Integer userId) throws UserException {
		if (!model.getUsers().containsKey(userId)) {
			throw new UserException(
					new StringBuilder().append(userId).append(" ").append(env.getProperty("MESSAGE_002")).toString());
		}

	}

}
