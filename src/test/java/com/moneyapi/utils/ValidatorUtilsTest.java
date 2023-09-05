package com.moneyapi.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import com.moneyapi.exception.TransactionException;
import com.moneyapi.model.DataModel;
import com.moneyapi.service.UserServiceImpl;

class ValidatorUtilsTest {

	@InjectMocks
	private ValidatorUtils validatorUtils;

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@Mock
	private Environment env;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Mock
	private DataModel dataModel;

	@Test
	void testValidateSenderId() {
		StringBuilder message = new StringBuilder();

		// validate sender Id : Case 1
		int senderId = -1;
		String receiverId = "1";
		String expectedErrorMessage = message.append(senderId).append(" ").append(env.getProperty("MESSAGE_003"))
				.toString();

		assertThrows(TransactionException.class, () -> validatorUtils.validate(senderId, receiverId, BigDecimal.ONE),
				expectedErrorMessage);
	}

//	@Test
//	void testValidateReceiverId() {
//		
//		dataModel.getUsers().put(1, new User(1, "vijay", BigDecimal.ONE));
//		StringBuilder message = new StringBuilder();
//
//		// validate receiver Id : Case 1
//		int senderId = 1;
//		String receiverId = "";
//		String expectedErrorMessage = message.append(receiverId).append(" ").append(env.getProperty("MESSAGE_004"))
//				.toString();
//
//		// Act & Assert
//		assertThrows(TransactionException.class, () -> validatorUtils.validate(senderId, receiverId, BigDecimal.ONE),
//				expectedErrorMessage);
//
//	}

}
