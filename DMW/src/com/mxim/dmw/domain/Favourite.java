package com.mxim.dmw.domain;

import java.io.Serializable;

public class Favourite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String colName, colValue, operator;

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColValue() {
		return colValue;
	}

	public void setColValue(String colValue) {
		this.colValue = colValue;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
