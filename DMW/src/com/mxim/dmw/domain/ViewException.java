package com.mxim.dmw.domain;

import java.io.Serializable;

public class ViewException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String shortMessage, fullMessage, exceptionNumber;

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

	public String getExceptionNumber() {
		return exceptionNumber;
	}

	public void setExceptionNumber(String exceptionNumber) {
		this.exceptionNumber = exceptionNumber;
	}

}
