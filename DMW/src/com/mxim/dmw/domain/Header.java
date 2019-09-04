package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Header implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<String, String> columnNames;
	private LinkedHashMap<String, Integer> columnDisplaySize;
	private LinkedHashMap<String, String> columnTypes;
	private LinkedHashMap<String, String> columnEdit;
	private LinkedHashMap<String, Integer> columnPrecision;

	public LinkedHashMap<String, String> getColumnEdit() {
		return columnEdit;
	}

	public void setColumnEdit(LinkedHashMap<String, String> columnEdit) {
		this.columnEdit = columnEdit;
	}

	public LinkedHashMap<String, String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(LinkedHashMap<String, String> columnNames) {
		this.columnNames = columnNames;
	}

	public LinkedHashMap<String, Integer> getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public void setColumnDisplaySize(LinkedHashMap<String, Integer> columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public LinkedHashMap<String, String> getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(LinkedHashMap<String, String> columnTypes) {
		this.columnTypes = columnTypes;
	}

	public LinkedHashMap<String, Integer> getColumnPrecision() {
		return columnPrecision;
	}

	public void setColumnPrecision(LinkedHashMap<String, Integer> columnPrecision) {
		this.columnPrecision = columnPrecision;
	}

}
