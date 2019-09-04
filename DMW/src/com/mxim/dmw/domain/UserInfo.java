package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserInfo() {
		// TODO Auto-generated constructor stub
	}

	private String userId, firstName, lastName, email, employeeNum;
	private boolean admin;
	private ArrayList<String> roles;

	public ArrayList<String> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<String> roles) {
		this.roles = roles;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmployeeNum() {
		return employeeNum;
	}

	public void setEmployeeNum(String employeeNum) {
		this.employeeNum = employeeNum;
	}

}
