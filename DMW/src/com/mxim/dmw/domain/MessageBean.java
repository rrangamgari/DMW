package com.mxim.dmw.domain;

import java.io.Serializable;

public class MessageBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key, value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MessageBean(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
