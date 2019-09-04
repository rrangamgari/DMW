package com.mxim.dmw.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DynamicBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DynamicBean() {
		// TODO Auto-generated constructor stub
	}

	private Header header;
	private int totalCount;
	private int pageCount;
	private TableInfo tableInfo;

	public TableInfo getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	private ArrayList<LinkedHashMap<String, String>> data;

	public ArrayList<LinkedHashMap<String, String>> getData() {
		return data;
	}

	public void setData(ArrayList<LinkedHashMap<String, String>> data) {
		this.data = data;
	}

}
