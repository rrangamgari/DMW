package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class TableInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pageLength, id, startRec, endRec, version;
	private String schemaName, tableName, tableOwner, tableAlias, roles, sortBy, sortDir, fixedFields, pivotColumns,
			pivotData, owner, query;
	private boolean addRow, deleteRow, massUpdate, upload, editable, pivot, paging, download, virtualPivot, condQuery,
			role, favouriteView;
	private ArrayList<Favourite> favoriteList;

	// used fro admin
	private String systemDate, systemUser;

	public ArrayList<Favourite> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(ArrayList<Favourite> favoriteList) {
		this.favoriteList = favoriteList;
	}

	public boolean isFavouriteView() {
		return favouriteView;
	}

	public void setFavouriteView(boolean favouriteView) {
		this.favouriteView = favouriteView;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(String systemDate) {
		this.systemDate = systemDate;
	}

	public String getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(String systemUser) {
		this.systemUser = systemUser;
	}

	public boolean isRole() {
		return role;
	}

	public void setRole(boolean role) {
		this.role = role;
	}

	public boolean isCondQuery() {
		return condQuery;
	}

	public void setCondQuery(boolean condQuery) {
		this.condQuery = condQuery;
	}

	public boolean isVirtualPivot() {
		return virtualPivot;
	}

	public void setVirtualPivot(boolean virtualPivot) {
		this.virtualPivot = virtualPivot;
	}

	public int getPageLength() {
		return pageLength;
	}

	public void setPageLength(int pageLength) {
		this.pageLength = pageLength;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStartRec() {
		return startRec;
	}

	public void setStartRec(int startRec) {
		this.startRec = startRec;
	}

	public int getEndRec() {
		return endRec;
	}

	public void setEndRec(int endRec) {
		this.endRec = endRec;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableOwner() {
		return tableOwner;
	}

	public void setTableOwner(String tableOwner) {
		this.tableOwner = tableOwner;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

	public String getFixedFields() {
		return fixedFields;
	}

	public void setFixedFields(String fixedFields) {
		this.fixedFields = fixedFields;
	}

	public String getPivotColumns() {
		return pivotColumns;
	}

	public void setPivotColumns(String pivotColumns) {
		this.pivotColumns = pivotColumns;
	}

	public String getPivotData() {
		return pivotData;
	}

	public void setPivotData(String pivotData) {
		this.pivotData = pivotData;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isAddRow() {
		return addRow;
	}

	public void setAddRow(boolean addRow) {
		this.addRow = addRow;
	}

	public boolean isDeleteRow() {
		return deleteRow;
	}

	public void setDeleteRow(boolean deleteRow) {
		this.deleteRow = deleteRow;
	}

	public boolean isMassUpdate() {
		return massUpdate;
	}

	public void setMassUpdate(boolean massUpdate) {
		this.massUpdate = massUpdate;
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

	public boolean isPivot() {
		return pivot;
	}

	public void setPivot(boolean pivot) {
		this.pivot = pivot;
	}

	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

}
