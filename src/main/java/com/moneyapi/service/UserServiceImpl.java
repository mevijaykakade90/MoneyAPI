package com.moneyapi.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.moneyapi.exception.UserException;
import com.moneyapi.model.DataModel;
import com.moneyapi.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private DataModel model;

	@Autowired
	private Environment env;

	@Override
	public User createUser(String name) {

		UserId id = new UserId(model.getUserId().getAndIncrement());
		model.getUsers().put(id.userId(), new User(id.userId(), name, INITIAL_BALANCE));

		return model.getUsers().get(id.userId());

	}

	@Override
	public User updateUser(Integer userId, BigDecimal amount) {

		model.getUsers().get(userId).setAmount(amount);
		return model.getUsers().get(userId);

	}

	@Override
	public String removeUser(Integer userId) {
		String name = model.getUsers().get(userId).getName();
		model.getUsers().remove(userId);
		return name;
	}

	@Override
	public BigDecimal getBalance(Integer userId) {
		if (model.getUsers().containsKey(userId)) {
			return model.getUsers().get(userId).getAmount();
		} else {
			throw new UserException(
					new StringBuilder().append(userId).append(" ").append(env.getProperty("MESSAGE_002")).toString());
		}
	}
}
