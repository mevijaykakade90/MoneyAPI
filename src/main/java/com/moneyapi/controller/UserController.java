package com.moneyapi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneyapi.exception.UserException;
import com.moneyapi.model.DataModel;
import com.moneyapi.model.User;
import com.moneyapi.service.UserService;
import com.moneyapi.utils.ValidatorUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

	@Autowired
	private DataModel model;

	@Autowired
	private UserService userService;

	@Autowired
	private ValidatorUtils utils;

	@Autowired
	private TaskExecutor taskExecutor;

	/**
	 * Handles GET requests to "/createUsers" endpoint. This method is responsible
	 * for creating new users in the system.
	 *
	 * @return A ResponseEntity containing the created user if successful, or an
	 *         error message with an HTTP status code indicating the failure.
	 */
	@PostMapping("/createUser/{name}")
	public ResponseEntity<?> createUser(@PathVariable String name) {

		try {
			utils.validate(name);
			taskExecutor.execute(() -> {
				var user = userService.createUser(name);
				log.info("{} user is created", user.getName());

			});

			return ResponseEntity.accepted().body("User is created sucessfully.");

		} catch (UserException e) {

			log.error("User Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

	}

	/**
	 * Updates user data based on provided parameters.
	 *
	 * @param userId The unique identifier of the user to update. (Required) If not
	 *               provided, the update will be applied to all users.
	 * @param amount The amount to update for user balances. (Required) If not
	 *               provided, no balance update will be performed.
	 * @return A ResponseEntity containing the updated user object or an error
	 *         response.
	 * 
	 */
	@PostMapping("/updateUser/{userId}/{amount}")
	public ResponseEntity<?> updateUser(@PathVariable Integer userId, @PathVariable BigDecimal amount) {
		// Use a lock to ensure sequential processing of requests

		try {
			utils.validate(userId, amount);
			taskExecutor.execute(() -> {
				var user = userService.updateUser(userId, amount);
				log.info("{} user is updated.", user);

			});

			return ResponseEntity.accepted().body("User is updated sucessfully.");

		} catch (UserException e) {

			log.error("User Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	/**
	 * Removes a user from the system based on their user ID.
	 *
	 * @param userId The unique identifier of the user to be removed. (Required)
	 * @return A response entity indicating the success or failure of the user
	 *         removal. If successful, returns the removed user's details. If an
	 *         error occurs, returns an internal server error with an error message.
	 */
	@PostMapping("/removeUser/{userId}")
	public ResponseEntity<?> removeUser(@PathVariable Integer userId) {

		try {
			utils.validate(userId);
			taskExecutor.execute(() -> {
				var user = userService.removeUser(userId);
				log.info("{} user is removed", user);

			});

			return ResponseEntity.accepted().body("User is removed sucessfully.");

		} catch (UserException e) {

			log.error("User Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

	}

	/**
	 * Retrieves the total number of users in the model.
	 *
	 * @return The number of users currently stored in the model.
	 */
	@GetMapping("/usersSize")
	public int usersSize() {
		return model.getUsers().size();
	}

	/**
	 * Retrieves a list of all users from the model and returns it.
	 *
	 * @return A list of User objects containing all the users in the model.
	 */
	@GetMapping("/printUsers")
	public List<User> printUsers() {
		return new ArrayList<>(model.getUsers().values());
	}

	/**
	 * Retrieves the balance for a user identified by their unique user ID.
	 *
	 * @param userId The unique identifier of the user whose balance is requested.
	 * @return A response containing the user's account balance if successful, or an
	 *         error response with a status code indicating the reason for failure.
	 * @throws UserException If an error occurs while retrieving the balance, such
	 *                       as an invalid user ID or a system error.
	 */
	@GetMapping("/getBalance/{userId}")
	public ResponseEntity<?> getBalance(@PathVariable Integer userId) {

		try {
			var balance = userService.getBalance(userId);
			log.info("{} user balcance is : {}", userId, balance.toString());
			return ResponseEntity.ok(balance);
		} catch (UserException e) {
			log.error("User Exception : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

}
