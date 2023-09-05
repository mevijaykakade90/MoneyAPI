package com.moneyapi.service;

import java.math.BigDecimal;

import com.moneyapi.model.User;

public interface UserService {

	final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(10000);

	record UserId(int userId) {
	}

	User createUser(String name);

	User updateUser(Integer userId, BigDecimal amount);

	String removeUser(Integer userId);

	BigDecimal getBalance(Integer userId);
}
