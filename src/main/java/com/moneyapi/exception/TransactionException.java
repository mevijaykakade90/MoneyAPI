package com.moneyapi.exception;

public class TransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TransactionException() {
		super("User already exists.");
	}

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionException(Throwable cause) {
		super(cause);
	}

}
