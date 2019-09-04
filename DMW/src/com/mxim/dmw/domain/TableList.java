package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class TableList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text, name, tableName;
	private boolean leaf, expanded = true;
	private int tableID, parentId;

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getTableID() {
		return tableID;
	}

	public void setTableID(int tableID) {
		this.tableID = tableID;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	private ArrayList<TableList> children;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public ArrayList<TableList> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TableList> children) {
		this.children = children;
	}

}
