package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class TableUsers implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TableUsers() {
		// TODO Auto-generated constructor stub
	}

	private String tableName;
	private ArrayList<String> users;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<String> users) {
		this.users = users;
	}

}
