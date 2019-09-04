package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class TableAccess implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roleName;
	private int roleId;
	private boolean addRow, delRow, massUpdate, download, upload, editable;
	private ArrayList<String> editableColumns;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public boolean isAddRow() {
		return addRow;
	}

	public void setAddRow(boolean addRow) {
		this.addRow = addRow;
	}

	public boolean isDelRow() {
		return delRow;
	}

	public void setDelRow(boolean delRow) {
		this.delRow = delRow;
	}

	public boolean isMassUpdate() {
		return massUpdate;
	}

	public void setMassUpdate(boolean massUpdate) {
		this.massUpdate = massUpdate;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public boolean isUpload() {
		return upload;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public ArrayList<String> getEditableColumns() {
		return editableColumns;
	}

	public void setEditableColumns(ArrayList<String> editableColumns) {
		this.editableColumns = editableColumns;
	}

}
