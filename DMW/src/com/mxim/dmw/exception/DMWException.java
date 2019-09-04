package com.mxim.dmw.exception;

import org.springframework.validation.Errors;

public class DMWException extends Exception {
	private Errors errors;

	public DMWException(String msg) {
		super(truncateMessage(msg));
		this.errors = errors;
	}

	public DMWException() {

		super("Error Message from DMW Application");

	}

	private static String truncateMessage(String message) {
		if (message.length() < 50) {
			return message;
		}

		return message.substring(0, 50) + "...";
	}

	public Errors getErrors() {
		return errors;
	}
}
