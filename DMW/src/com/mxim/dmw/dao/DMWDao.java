package com.mxim.dmw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mxim.dmw.domain.DynamicBean;
import com.mxim.dmw.domain.Favourite;
import com.mxim.dmw.domain.Header;
import com.mxim.dmw.domain.SimpleComboBean;
import com.mxim.dmw.domain.TableInfo;
import com.mxim.dmw.domain.TableList;
import com.mxim.dmw.domain.UserInfo;
import com.mxim.dmw.domain.ViewException;
import com.mxim.dmw.exception.DMWException;
import com.mxim.dmw.util.AuditMessage;
import com.mxim.dmw.util.ConnectionUtil;
import com.mxim.dmw.util.Constants;
import com.mxim.dmw.util.Logger;

public class DMWDao {
	// DMWAuditDao audit = new DMWAuditDao();
	DMWMessageQueue audit = new DMWMessageQueue();

	public UserInfo getUserDetails(String userId) throws DMWException {
		Logger.start(this, "getUserDetails userId:" + userId);
		long la = System.currentTimeMillis();
		String qry = "SELECT * FROM APP_USER_PROFILE where upper(user_id) like upper('" + userId + "') ";
		audit.insertAuditLog("getUserDetails", "Start of the user details", qry, userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getUserDetails", "userId:" + userId, null, null);
		Logger.info(this, "getUserDetails", "Qry :" + qry, null, null);
		audit.insertAuditLog("getUserDetails", "Getting the user details", qry, userId);
		UserInfo user = new UserInfo();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			while (rs.next()) {
				user.setEmail(rs.getString("EMAIL_ID"));
				user.setFirstName(rs.getString("first_Name"));
				user.setLastName(rs.getString("LAST_NAME"));
				user.setEmployeeNum(rs.getString("EMPLOYEE_NO"));
				user.setUserId(userId);
			}
			audit.insertAuditLog("getUserDetails", "Got the user details", qry, userId);

			qry = "select * from app_user_sub_roles where SUB_ROLE='DMW_ADMIN' and  upper(user_id) like upper('"
					+ userId + "') ";
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			audit.insertAuditLog("getUserDetails", "Checking if user is Admin", qry, userId);
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			if (rs.next()) {
				user.setAdmin(true);
				audit.insertAuditLog("getUserDetails", "User is Admin", qry, userId);
			}
			qry = "SELECT a.ROLE_NAME, b.*  FROM I2WEBDATA.DMW_ROLES a, I2WEBDATA.DMW_USER_ROLES b"
					+ " WHERE a.DMW_ROLE_ID = b.DMW_ROLE_ID and b.EMPLOYEE_NO='" + user.getEmployeeNum() + "' ";
			if (user.isAdmin()) {
				qry = "SELECT * FROM I2WEBDATA.DMW_ROLES";
			}
			audit.insertAuditLog("getUserDetails", "Fetching the user roles", qry, userId);
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			audit.insertAuditLog("getUserDetails", "got the user roles", qry, userId);
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			ArrayList<String> roles = new ArrayList<>();
			while (rs.next()) {
				String role = rs.getString("DMW_ROLE_ID");
				roles.add(role);
				audit.insertAuditLog("getUserDetails", "User Roles :" + role, qry, userId);
			}
			user.setRoles(roles);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getUserDetails", "Sql exception", null, se);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "getUserDetails()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getUserDetails", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getUserDetails userId:" + userId);
		audit.insertAuditLog("getUserDetails", "End of the user details", qry, userId);
		return user;
	}

	public ArrayList<TableList> getParentNameList(String userId, boolean admin, String roles) throws DMWException {
		Logger.start(this, "getParentNameList ");
		long la = System.currentTimeMillis();

		audit.insertAuditLog("getParentNameList", "Start of the Table List", "", userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;

		String qry = Constants.DMW_CONF_LIST_TREE_QUERY.replaceAll(":ROLES", roles);
		if (admin)
			qry = Constants.DMW_CONF_LIST_QUERY;
		Logger.info(this, "getParentNameList", "Qry :" + qry, null, null);
		ArrayList<TableList> al = new ArrayList<TableList>();
		try {

			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			audit.insertAuditLog("getParentNameList", "Getting the Table Information in tree list",
					Constants.DMW_CONF_LIST_QUERY, userId);
			while (rs.next()) {
				TableList list = new TableList();
				list.setName(rs.getString("CONF_NAME"));
				list.setParentId(rs.getInt("PARENT_ID"));
				list.setTableID(rs.getInt("DMW_TABLE_CONF_ID"));
				list.setText(rs.getString("CONF_NAME"));
				list.setLeaf(false);
				System.out.println("-------   " + rs.getString("CONF_NAME"));
				al.add(list);
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getParentNameList", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getParentNameList()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getParentNameList", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getParentNameList");
		audit.insertAuditLog("getParentNameList", "End of the Parent Names List", "", userId);
		return al;
	}

	public ArrayList<TableList> getTableNameList(String userId, String roles) throws DMWException {
		Logger.start(this, "getTableNameList ");
		long la = System.currentTimeMillis();
		// String qry = "SELECT * FROM I2WEBDATA.DMW_TABLE_INFO WHERE DMW_ROLE
		// IN (" + roles + ") ";
		String qry = Constants.DMW_TABLES_NAME_LIST_QUERY.replaceAll(":ROLES", roles);
		audit.insertAuditLog("getTableNameList", "Start of the Table List", "", userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getTableNameList", "Qry :" + qry, null, null);
		ArrayList<TableList> al = new ArrayList<TableList>();
		try {

			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			audit.insertAuditLog("getTableNameList", "Getting the Table Information in tree list", qry, userId);
			while (rs.next()) {
				TableList list = new TableList();
				list.setName(rs.getString("DMW_TABLE_INFO_ID"));
				list.setText(rs.getString("TABLE_ALIAS"));
				list.setTableID(rs.getInt("DMW_TABLE_INFO_ID"));
				list.setParentId(rs.getInt("DMW_TABLE_CONF_ID"));
				list.setLeaf(true);
				al.add(list);
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableNameList", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getTableNameList()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getTableNameList", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableNameList");
		audit.insertAuditLog("getTableNameList", "End of the Table Information", "", userId);
		return al;
	}

	public DynamicBean getTableInfo(String jsonLine, TableInfo tableInfo, String userId) throws DMWException {
		Logger.start(this, "getTableInfo table Id:" + tableInfo);
		audit.insertAuditLog("getTableInfo", "Starts the Table Information", "", userId);
		long la = System.currentTimeMillis();
		DynamicBean dbean = new DynamicBean();
		// TableInfo tableInfo = getTableInformation(tableId);
		String filter = "   WHERE 1=1   ";
		if (jsonLine != null) {
			JsonParser json = new JsonParser();
			JsonArray jArr = (JsonArray) json.parse(jsonLine);
			for (int i = 0; i < jArr.size(); i++) {
				JsonObject jObj = new JsonObject();
				jObj = (JsonObject) jArr.get(i);
				String value = "";
				if (jObj.get("value") != null)
					value = jObj.get("value").getAsString();
				if (jObj.get("operator").getAsString().equalsIgnoreCase("gt")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  >  " + value + "";
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("lt")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  <  " + value + "";
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("eq")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  =  " + value + "";
				} else {
					filter += "   AND UPPER(" + (jObj.get("property").getAsString()).toLowerCase() + ")  "
							+ jObj.get("operator").getAsString() + "  UPPER('" + value + "%')";
				}
			}
		}
		String qry = "SELECT * FROM " + tableInfo.getOwner() + "." + tableInfo.getTableName() + filter;
		audit.insertAuditLog("getTableInfo", "Frame the Query", qry, userId);
		int k = 1;

		if (tableInfo.isPaging()) {
			audit.insertAuditLog("getTableInfo", "Frame the Query if pagination is true", "", userId);
			// qry = getPaginationQuery(tableInfo, filter);
			audit.insertAuditLog("getTableInfo", "Framed the Query with pagination", qry, userId);
			audit.insertAuditLog("getTableInfo", "Getting the page length", "", userId);
			dbean.setPageCount(tableInfo.getPageLength());
			audit.insertAuditLog("getTableInfo", "Got the page length and count is : " + dbean.getPageCount(), "",
					userId);
			k = 2;
		} else {
			audit.insertAuditLog("getTableInfo", "Getting the page length without pagination ", "", userId);
			dbean.setPageCount(getPaginationCount(tableInfo));
			audit.insertAuditLog("getTableInfo", "Got the page length and count is : " + dbean.getPageCount(), "",
					userId);

		}
		if (tableInfo.isPivot()) {
			audit.insertAuditLog("getTableInfo", "Frame the Query if pivot is true", "", userId);
			qry = getPivotQuery(tableInfo, filter);
			audit.insertAuditLog("getTableInfo", "Framed the Query with pivot", qry, userId);
			audit.insertAuditLog("getTableInfo", "Getting the page length", "", userId);
			dbean.setPageCount(getPivotPageCount(qry));
			audit.insertAuditLog("getTableInfo", "Got the page length and count is : " + dbean.getPageCount(), "",
					userId);
		}
		ResultSet rs = null;
		PreparedStatement smt = null;

		Header header = new Header();
		LinkedHashMap<String, String> columnNames = new LinkedHashMap<>();
		columnNames.put("RID", "ROWID");
		LinkedHashMap<String, Integer> columnDisplaySize = new LinkedHashMap<>();
		LinkedHashMap<String, String> columnTypes = new LinkedHashMap<>();
		columnTypes.put("RID", "string");
		LinkedHashMap<String, String> columnEdit = new LinkedHashMap<>();
		LinkedHashMap<String, Integer> columnPrecision = new LinkedHashMap<>();
		Connection con = null;
		Logger.info(this, "getTableInfo", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getTableInfo", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			if (tableInfo.isPivot()) {
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = k; i <= rsmd.getColumnCount(); i++) {
					rsmd.getColumnDisplaySize(i); //
					System.out.println(rsmd.getColumnType(i) + "|" + //
							rsmd.getColumnLabel(i));
					columnNames.put(rsmd.getColumnLabel(i), rsmd.getColumnName(i));
					columnTypes.put(rsmd.getColumnLabel(i), getType(rsmd.getColumnType(i)));
					columnDisplaySize.put(rsmd.getColumnLabel(i), rsmd.getColumnDisplaySize(i));
					columnPrecision.put(rsmd.getColumnLabel(i), rsmd.getPrecision((i)));
					if (tableInfo.isEditable())
						columnEdit.put(rsmd.getColumnLabel(i), "Y");
					else
						columnEdit.put(rsmd.getColumnLabel(i), "N");
				}

			} else {
				qry = "SELECT * FROM ALL_TAB_COLUMNS WHERE table_name = '" + tableInfo.getTableName().toUpperCase()
						+ "' and owner = '" + tableInfo.getOwner().toUpperCase() + "' ORDER BY column_id";
				Logger.info(this, "getTableInfo", "Qry :" + qry, null, null);
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();

				while (rs.next()) {
					columnNames.put(rs.getString("COLUMN_NAME"), rs.getString("COLUMN_NAME"));
					columnTypes.put(rs.getString("COLUMN_NAME"), getType(rs.getString("DATA_TYPE")));
					columnDisplaySize.put(rs.getString("COLUMN_NAME"), rs.getInt("DATA_LENGTH"));
					columnPrecision.put(rs.getString("COLUMN_NAME"), rs.getInt("DATA_SCALE"));
					if (rs.getString("DATA_TYPE").equalsIgnoreCase("NUMBER")) {
						columnDisplaySize.put(rs.getString("COLUMN_NAME"),
								rs.getInt("DATA_PRECISION") - rs.getInt("DATA_SCALE"));
					}
					if (tableInfo.isEditable())
						columnEdit.put(rs.getString("COLUMN_NAME"), "Y");
					else
						columnEdit.put(rs.getString("COLUMN_NAME"), "N");
					if (!tableInfo.isPaging()) {
						tableInfo.setSortBy(rs.getString("COLUMN_NAME"));
						tableInfo.setSortDir("ASC");
					}
				}

			}

			header.setColumnDisplaySize(columnDisplaySize);
			header.setColumnNames(columnNames);
			header.setColumnPrecision(columnPrecision);
			header.setColumnTypes(columnTypes);
			qry = "SELECT cols.table_name, cols.column_name, cols.position, cons.status, cons.owner"
					+ "  FROM all_constraints cons, all_cons_columns cols WHERE cols.table_name = '"
					+ tableInfo.getTableName() + "' AND cons.constraint_type = 'P' and cons.owner = '"
					+ tableInfo.getOwner() + "' AND cons.constraint_name = cols.constraint_name"
					+ " AND cons.owner = cols.owner ORDER BY cols.table_name, cols.position";
			Logger.info(this, "getTableInfo", "Qry :" + qry, null, null);
			smt.close();
			rs.close();
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			String virtualRowId = "''";
			while (rs.next()) {
				virtualRowId += "||" + rs.getString("COLUMN_NAME");
				// columnEdit.put(rs.getString("COLUMN_NAME"), "N");
			}
			if (!virtualRowId.equalsIgnoreCase("''")) {
				columnNames.put("RID", virtualRowId);
			}
			header.setColumnEdit(columnEdit);
			dbean.setHeader(header);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableInfo", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getTableInfo()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getTableInfo", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableInfo " + tableInfo);
		return dbean;
	}

	@SuppressWarnings("resource")
	public TableInfo getTableInformation(String tableId, ArrayList<String> roles, String userId) throws DMWException {
		Logger.start(this, "getTableInformation tableId:" + tableId);
		long la = System.currentTimeMillis();
		String roleId = "-1";
		for (String role : roles) {
			roleId += "," + role;
		}
		String qry = Constants.DMW_TABLES_INFORMATION_QUERY.replaceAll(":DMW_TABLE_INFO_ID", tableId)
				.replaceAll(":DMW_TABLE_ROLES_ID", roleId).replaceAll(":USER_NAME", userId);
		TableInfo info = new TableInfo();
		ResultSet rs = null;
		PreparedStatement smt = null;

		Connection con = null;
		Logger.info(this, "getTableInformation", "tableId:" + tableId, null, null);
		Logger.info(this, "getTableInformation", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			while (rs.next()) {
				if (rs.getString("ADD_ROW") != null && rs.getString("ADD_ROW").equalsIgnoreCase("Y"))
					info.setAddRow(true);
				if (rs.getString("DELETE_ROW") != null && rs.getString("DELETE_ROW").equalsIgnoreCase("Y"))
					info.setDeleteRow(true);
				if (rs.getString("UPLOAD") != null && rs.getString("UPLOAD").equalsIgnoreCase("Y"))
					info.setUpload(true);
				if (rs.getString("DOWNLOAD") != null && rs.getString("DOWNLOAD").equalsIgnoreCase("Y"))
					info.setDownload(true);
				if (rs.getString("PAGINATION") != null && rs.getString("PAGINATION").equalsIgnoreCase("Y"))
					info.setPaging(true);
				if (rs.getString("EDITABLE") != null && rs.getString("EDITABLE").equalsIgnoreCase("Y"))
					info.setEditable(true);
				if (rs.getString("PIVOT") != null && rs.getString("PIVOT").equalsIgnoreCase("Y"))
					info.setVirtualPivot(true);
				if (rs.getString("FAVOURITE") != null && rs.getString("FAVOURITE").equalsIgnoreCase("Y"))
					info.setFavouriteView(true);
				if (rs.getString("MASS_UPDATE") != null && rs.getString("MASS_UPDATE").equalsIgnoreCase("Y"))
					info.setMassUpdate(true);
				info.setPivot(false);
				info.setId(rs.getInt("DMW_TABLE_INFO_ID"));
				info.setSchemaName(rs.getString("schema_Name"));
				info.setTableName(rs.getString("TABLE_NAME"));
				info.setTableOwner(rs.getString("TABLE_OWNER"));
				info.setTableAlias(rs.getString("TABLE_ALIAS"));
				// info.setRoles(rs.getString("ROLES"));
				info.setPageLength(rs.getInt("PAGE_LENGTH"));
				info.setSortBy(rs.getString("SORT_BY"));
				info.setSortDir(rs.getString("SORT_DIR"));
				info.setFixedFields(rs.getString("FIXED_FIELDS"));
				info.setPivotColumns(rs.getString("PIVOT_COLUMNS"));
				info.setPivotData(rs.getString("PIVOT_DATA"));
				info.setOwner(rs.getString("SCHEMA_NAME"));
				info.setQuery(rs.getString("CONDITIONAL_QUERY"));
				info.setVersion(rs.getInt("VERSION"));
			}
			if (info.getQuery().equalsIgnoreCase("Y")) {
				// qry = " SELECT * FROM I2WEBDATA.DMW_CONDITIONAL_QUERY where
				// DMW_TABLE_INFO_ID=" + info.getId();
				qry = Constants.DMW_CONDITIONAL_QUERY.replaceAll(":DMW_TABLE_INFO_ID", info.getId() + "");

				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				if (rs.next()) {
					info.setQuery(rs.getString("QUERY"));
				}
			}
			if (info.isFavouriteView()) {
				qry = Constants.DMW_TABLE_FAVOURITE_QUERY.replaceAll(":DMW_TABLE_INFO_ID", info.getId() + "")
						.replaceAll(":USER_NAME", userId);
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				ArrayList<Favourite> al = new ArrayList<>();
				while (rs.next()) {
					Favourite fav = new Favourite();
					fav.setColName(rs.getString("COLUMN_NAME"));
					fav.setColValue(rs.getString("COLUMN_VALUE"));
					fav.setOperator(rs.getString("COLUMN_OPERATOR"));
					al.add(fav);
				}
				info.setFavoriteList(al);
			}
			audit.insertAuditLog("getTableInformation", "getTableInformation Dao", "", userId);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableInformation", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getTableInformation()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getTableInformation", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableInformation " + tableId);

		return info;
	}

	public String getPaginationQuery(TableInfo tableInfo, String filter, LinkedHashMap<String, String> columnNames) {
		Logger.start(this, "checkPagination tableInfo:" + tableInfo);
		long la = System.currentTimeMillis();
		Logger.info(this, "checkPagination", "tableInfo:" + tableInfo, null, null);
		String qry = "SELECT *  FROM (SELECT ROW_NUMBER () OVER (ORDER BY " + tableInfo.getSortBy().toLowerCase() + " "
				+ tableInfo.getSortDir() + ") rn, " + columnNames.get("RID")
				+ " as RID, a.*          FROM (  SELECT *                   FROM " + tableInfo.getOwner() + "."
				+ tableInfo.getTableName() + filter + "                ORDER BY " + tableInfo.getSortBy().toLowerCase()
				+ " " + tableInfo.getSortDir() + ") a)    WHERE rn >= " + tableInfo.getStartRec() + " AND rn <= "
				+ tableInfo.getEndRec();
		if (!tableInfo.getQuery().equalsIgnoreCase("N")) {
			qry = "SELECT *  FROM (SELECT ROW_NUMBER () OVER (ORDER BY " + tableInfo.getSortBy().toLowerCase() + " "
					+ tableInfo.getSortDir() + ") rn, " + columnNames.get("RID")
					+ " as RID, a.*          FROM (  SELECT *                   FROM " + tableInfo.getQuery() + filter
					+ "                ORDER BY " + tableInfo.getSortBy().toLowerCase() + " " + tableInfo.getSortDir()
					+ ") a)    WHERE rn >= " + tableInfo.getStartRec() + " AND rn <= " + tableInfo.getEndRec();
		}
		long lb = System.currentTimeMillis();
		Logger.info(this, "checkPagination", "Qry :" + qry, null, null);
		Logger.info(this, "checkPagination", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "checkPagination " + tableInfo);
		audit.insertAuditLog("checkPagination", "checkPagination Dao", qry, "");
		return qry;
	}

	public int getPaginationCount(TableInfo tableInfo) throws DMWException {
		Logger.start(this, "getPaginationCount tableInfo:" + tableInfo);
		long la = System.currentTimeMillis();
		String qry = "SELECT count(*) from " + tableInfo.getOwner() + "." + tableInfo.getTableName();
		if (!tableInfo.getQuery().equalsIgnoreCase("N")) {
			qry = "SELECT count(*)  FROM " + tableInfo.getQuery();
		}
		int id = -1;
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getPaginationCount", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getPaginationCount", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getPaginationCount", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getPaginationCount()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getPaginationCount", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getPaginationCount " + tableInfo);
		audit.insertAuditLog("getPaginationCount", "getPaginationCount Dao" + id, qry, "");
		return id;
	}

	public int getPivotPageCount(String qry) throws DMWException {
		Logger.start(this, "getPivotPageCount qry:" + qry);
		long la = System.currentTimeMillis();
		int count = -1;
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getPivotPageCount", "qry:" + qry, null, null);
		Logger.info(this, "getPivotPageCount", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			qry = "SELECT count(*) from (" + qry + " ) ";
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getPivotPageCount", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getPivotPageCount()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getPivotPageCount", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getPivotPageCount ");
		audit.insertAuditLog("getPivotPageCount", "getPivotPageCount Dao" + count, qry, "");
		return count;
	}

	public String getPivotQuery(TableInfo tableInfo, String filter) throws DMWException {
		Logger.start(this, "getPivotQuery tableInfo:" + tableInfo);
		long la = System.currentTimeMillis();
		String qry = "SELECT Distinct " + tableInfo.getPivotColumns() + " FROM " + tableInfo.getOwner() + "."
				+ tableInfo.getTableName() + " order by 1";
		ResultSet rs = null;
		PreparedStatement smt = null;
		ArrayList<String> al = new ArrayList<>();
		Connection con = null;
		Logger.info(this, "getPivotQuery", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getPivotQuery", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			while (rs.next()) {
				al.add(rs.getString(1));
			}

			String ww = al.toString().replaceAll("\\[", "\\('").replaceAll("\\]", "'\\)").replaceAll(",", "','")
					.replaceAll(" ", "");
			qry = "SELECT " + tableInfo.getFixedFields().replaceAll(",", "|| '|' || ")
					+ " AS rid, a.*  FROM (SELECT *  FROM (SELECT " + tableInfo.getFixedFields() + ","
					+ tableInfo.getPivotData() + "," + tableInfo.getPivotColumns() + " FROM " + tableInfo.getOwner()
					+ "." + tableInfo.getTableName() + filter + ") PIVOT (SUM (" + tableInfo.getPivotData() + ")"
					+ "                                      FOR (" + tableInfo.getPivotColumns() + ")       IN       "
					+ ww.trim() + "                       ) )a";
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getPivotQuery", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getPivotQuery()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getPivotQuery", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getPivotQuery " + tableInfo);
		audit.insertAuditLog("getPivotQuery", "getPivotQuery Dao", qry, "");
		return qry;
	}

	public static String getType(int columnNumber) {
		String str = "string";
		if (columnNumber == Types.VARCHAR || columnNumber == Types.CHAR || columnNumber == Types.LONGNVARCHAR) {
			str = "string";
		} else if (columnNumber == Types.NUMERIC || columnNumber == Types.DECIMAL) {
			str = "number";
			// str = "BigDecimal";//
		} else if (columnNumber == Types.BIT) {
			str = "boolean";
		} else if (columnNumber == Types.TINYINT) {
			str = "byte";
		} else if (columnNumber == Types.SMALLINT) {
			str = "short";
		} else if (columnNumber == Types.INTEGER) {
			str = "number";
		} else if (columnNumber == Types.BIGINT) {
			str = "long";
		} else if (columnNumber == Types.REAL || columnNumber == Types.FLOAT || columnNumber == Types.DOUBLE) {
			str = "double";
		} else if (columnNumber == Types.BINARY || columnNumber == Types.VARBINARY
				|| columnNumber == Types.LONGVARBINARY) {
			str = "byte[]";
		} else if (columnNumber == Types.DATE || columnNumber == Types.TIME || columnNumber == Types.TIMESTAMP) {
			str = "date";
		}
		return str;
	}

	public static String getType(String dataType) {
		String str = "string";
		if (dataType.equalsIgnoreCase("VARCHAR2") || dataType.equalsIgnoreCase("CHAR")) {
			str = "string";
		} else if (dataType.equalsIgnoreCase("NUMBER")) {
			str = "number";
			// str = "BigDecimal";//
		} else if (dataType.equalsIgnoreCase("DATE") || dataType.contains("TIMESTAMP")) {
			str = "date";
		}
		return str;
	}

	public DynamicBean getTableDetails(String jsonLine, TableInfo tableInfo, LinkedHashMap<String, String> columnNames,
			LinkedHashMap<String, String> columnTypes) throws DMWException {
		Logger.start(this, "getTableDetails table Id:" + tableInfo);
		long la = System.currentTimeMillis();
		DynamicBean dbean = new DynamicBean();
		// TableInfo tableInfo = getTableDetailsrmation(tableId);
		String filter = "   WHERE 1=1   ";
		if (jsonLine != null) {
			JsonParser json = new JsonParser();
			JsonArray jArr = (JsonArray) json.parse(jsonLine);
			for (int i = 0; i < jArr.size(); i++) {
				JsonObject jObj = new JsonObject();
				jObj = (JsonObject) jArr.get(i);
				String value = "";
				if (jObj.get("value") != null)
					value = jObj.get("value").getAsString();
				if (jObj.get("operator").getAsString().equalsIgnoreCase("gt")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  >  " + value + "";
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("lt")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  <  " + value + "";
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("eq")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  =  " + value + "";
				} else {
					filter += "   AND UPPER(" + (jObj.get("property").getAsString()).toLowerCase() + ")  "
							+ jObj.get("operator").getAsString() + "  UPPER('" + value + "%')";
				}
			}
		}
		String qry = "SELECT *  FROM (SELECT a.*, " + columnNames.get("RID") + " rid FROM " + tableInfo.getOwner() + "."
				+ tableInfo.getTableName() + " a " + filter + ") ORDER BY " + tableInfo.getSortBy() + "   "
				+ tableInfo.getSortDir();

		if (tableInfo.isPivot()) {
			qry = getPivotQuery(tableInfo, filter);
			dbean.setTotalCount(getPivotPageCount(qry));
		} else if (tableInfo.isPaging()) {
			qry = getPaginationQuery(tableInfo, filter, columnNames);
			dbean.setTotalCount(getPaginationCount(tableInfo, filter));
		} else
			dbean.setTotalCount(getPaginationCount(tableInfo, filter));
		if (!tableInfo.getQuery().equalsIgnoreCase("N")) {
			qry = "SELECT *  FROM (SELECT ROW_NUMBER () OVER (ORDER BY " + tableInfo.getSortBy().toLowerCase() + " "
					+ tableInfo.getSortDir() + ") rn, " + columnNames.get("RID")
					+ " as RID, a.*          FROM (  SELECT *                   FROM " + tableInfo.getQuery() + filter
					+ "                ORDER BY " + tableInfo.getSortBy().toLowerCase() + " " + tableInfo.getSortDir()
					+ ") a)    ";
		}
		ResultSet rs = null;
		PreparedStatement smt = null;

		ArrayList<LinkedHashMap<String, String>> al = new ArrayList<>();
		Connection con = null;
		Logger.info(this, "getTableDetails", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getTableDetails", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			rs.setFetchSize(100);

			while (rs.next()) {
				LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
				Collection<String> c = columnNames.keySet();
				Iterator<String> itr = c.iterator();
				while (itr.hasNext()) {
					String columnName = itr.next();
					if (columnTypes.get(columnName).equalsIgnoreCase("date")) {
						if (rs.getDate(columnName) != null)
							lhm.put(columnName, rs.getDate(columnName) + "");
						else
							lhm.put(columnName, "");
					} else {
						lhm.put(columnName, rs.getString(columnName));
					}

				}
				al.add(lhm);
			}
			dbean.setData(al);

			// dbean.setHeader(header);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableDetails", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getTableDetails()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getTableDetails", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableDetails " + tableInfo);
		audit.insertAuditLog("getTableDetails", "getTableDetails Dao", qry, "");
		return dbean;
	}

	private int getPaginationCount(TableInfo tableInfo, String filter) throws DMWException {

		Logger.start(this, "getPaginationCount tableInfo:" + tableInfo);
		long la = System.currentTimeMillis();
		String qry = "SELECT count(*) from " + tableInfo.getOwner() + "." + tableInfo.getTableName() + filter;
		if (!tableInfo.getQuery().equalsIgnoreCase("N")) {
			qry = "SELECT count(*)  FROM " + tableInfo.getQuery() + filter;
		}
		int id = -1;
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getPaginationCount", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getPaginationCount", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getPaginationCount", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getPaginationCount()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getPaginationCount", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getPaginationCount " + tableInfo);
		audit.insertAuditLog("getPaginationCount", "getPaginationCount Dao : " + id, qry, "");
		return id;

	}

	public boolean executeTableSave(TableInfo tableInfo, ArrayList<String> querylist, String userId)
			throws DMWException {
		Logger.start(this, "executeTableSave table Id:" + querylist);
		long la = System.currentTimeMillis();
		Connection con = null;
		ResultSet rs = null;
		Statement smt = null;
		boolean bool = false;
		Logger.info(this, "executeTableSave", "tableInfo:" + querylist, null, null);
		audit.insertAuditLog("executeTableSave", "Start of the executeTableSave", "Querylist", userId);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			con.setAutoCommit(false);
			smt = con.createStatement();
			for (String query : querylist) {
				Logger.info(this, "executeTableSave", "Qry :" + query, null, null);
				smt.addBatch(query);
			}
			int[] i = smt.executeBatch();
			con.commit();
			bool = true;
			synchronized (tableInfo) {
				tableInfo.setVersion(tableInfo.getVersion() + 1);
				changeVersion(tableInfo.getId(), tableInfo.getVersion(), userId);

			}
		} catch (SQLException se) {
			se.printStackTrace();
			audit.insertAuditLog("DMWException", "ORA-" + se.getErrorCode(), se.getMessage(), userId);
			Logger.error(this, "executeTableSave", "Sql exception", null, se);
			throw new DMWException(se.getMessage());
		} catch (DMWException e) {
			//
			Logger.error(this, "executeTableSave()", "DMWException", null, e);
			// throw new DMWException(e.getMessage());
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "executeTableSave", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "executeTableSave " + querylist);
		audit.insertAuditLog("executeTableSave", "End of the executeTableSave", "Querylist", userId);
		return bool;
	}

	@SuppressWarnings("resource")
	public DynamicBean getEditableColumns(DynamicBean dBean, TableInfo tableInfo) throws DMWException {
		Logger.start(this, "getEditableColumns tableInfo:" + tableInfo);
		long la = System.currentTimeMillis();
		String qry = Constants.DMW_EDITABLE_COLUMNS_QUERY.replaceAll(":TABLE_ID", tableInfo.getId() + "")
				.replaceAll(":TABLE_NAME", tableInfo.getTableName() + "")
				.replaceAll(":TABLE_OWNER", tableInfo.getOwner() + "");
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getEditableColumns", "tableId:" + tableInfo.getId(), null, null);
		Logger.info(this, "getEditableColumns", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");

			if (tableInfo.isPivot()) {
				qry = "SELECT Distinct " + tableInfo.getPivotColumns() + " FROM " + tableInfo.getOwner() + "."
						+ tableInfo.getTableName() + " order by 1";
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString(1) + "|Y");
					dBean.getHeader().getColumnEdit().put(rs.getString(1), "Y");
				}
			} else {
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				while (rs.next()) {
					// System.out.println(rs.getString("COLUMN_NAME") + "|" +
					// rs.getString("ALLOW_EDITABLE"));
					dBean.getHeader().getColumnEdit().put(rs.getString("COLUMN_NAME"), rs.getString("ALLOW_EDITABLE"));
				}
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getEditableColumns", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getEditableColumns()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getEditableColumns", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getEditableColumns " + dBean);

		return dBean;
	}

	public ViewException viewException(String userId) throws DMWException {
		Logger.start(this, "viewException userId:" + userId);
		long la = System.currentTimeMillis();
		String qry = "SELECT *  FROM (  SELECT *            FROM I2WEBDATA.DMW_AUDIT_TRAIL"
				+ "           WHERE event_name = 'DMWException' AND  upper(UPDATED_BY) like upper('" + userId + "')"
				+ "        ORDER BY updated_date DESC) WHERE ROWNUM < 2  ";
		audit.insertAuditLog("viewException", "Start of the viewException", qry, userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "viewException", "userId:" + userId, null, null);
		Logger.info(this, "viewException", "Qry :" + qry, null, null);
		audit.insertAuditLog("viewException", "Getting the viewException", qry, userId);
		ViewException vExp = new ViewException();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			if (rs.next()) {
				vExp.setExceptionNumber(rs.getString("EVENT_DESCRIPTION"));
				vExp.setFullMessage(rs.getString("EVENT_QUERY"));
				vExp.setShortMessage(rs.getString("EVENT_DESCRIPTION"));
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "viewException", "Sql exception", null, se);
			audit.insertAuditLog("viewException", "Exception while retriving the Error", qry, userId);
			throw new DMWException("Internal server error has occured.");

		} catch (DMWException e) {
			Logger.error(this, "viewException()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "viewException", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "viewException userId:" + userId);
		audit.insertAuditLog("viewException", "End of the View Exception", qry, userId);
		return vExp;
	}

	public DynamicBean getTableAllDetails(String jsonLine, TableInfo tableInfo,
			LinkedHashMap<String, String> columnNames) throws DMWException {
		Logger.start(this, "getTableDetails table Id:" + tableInfo);
		long la = System.currentTimeMillis();
		DynamicBean dbean = new DynamicBean();
		// TableInfo tableInfo = getTableDetailsrmation(tableId);
		String filter = "   WHERE 1=1   ";
		if (jsonLine != null) {
			JsonParser json = new JsonParser();
			JsonArray jArr = (JsonArray) json.parse(jsonLine);
			for (int i = 0; i < jArr.size(); i++) {
				JsonObject jObj = new JsonObject();
				jObj = (JsonObject) jArr.get(i);
				String value = "";
				if (jObj.get("value") != null)
					value = jObj.get("value").getAsString();
				if (jObj.get("operator").getAsString().equalsIgnoreCase("gt")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  >  " + value + "";
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("lt")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  <  " + value + "";
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("eq")) {
					filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  =  " + value + "";
				} else {
					filter += "   AND UPPER(" + (jObj.get("property").getAsString()).toLowerCase() + ")  "
							+ jObj.get("operator").getAsString() + "  UPPER('" + value + "%')";
				}
			}
		}
		String qry = "SELECT *  FROM (SELECT a.*, " + columnNames.get("RID") + " rid FROM " + tableInfo.getOwner() + "."
				+ tableInfo.getTableName() + " a " + filter + ") ORDER BY " + tableInfo.getSortBy() + "   "
				+ tableInfo.getSortDir();

		if (tableInfo.isPivot()) {
			qry = getPivotQuery(tableInfo, filter);
			dbean.setTotalCount(getPivotPageCount(qry));
		} else if (tableInfo.isPaging()) {
			tableInfo.setEndRec(getPaginationCount(tableInfo));
			qry = getPaginationQuery(tableInfo, filter, columnNames);
			dbean.setTotalCount(getPaginationCount(tableInfo, filter));
		} else
			dbean.setTotalCount(getPaginationCount(tableInfo));
		ResultSet rs = null;
		PreparedStatement smt = null;

		ArrayList<LinkedHashMap<String, String>> al = new ArrayList<>();
		Connection con = null;
		Logger.info(this, "getTableDetails", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getTableDetails", "Qry :" + qry, null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			rs = smt.executeQuery();
			rs.setFetchSize(100);
			while (rs.next()) {
				LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
				Collection<String> c = columnNames.keySet();
				Iterator<String> itr = c.iterator();
				while (itr.hasNext()) {
					String columnName = itr.next();
					lhm.put(columnName, rs.getString(columnName));
				}
				al.add(lhm);
			}
			dbean.setData(al);

			// dbean.setHeader(header);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableDetails", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getTableDetails()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getTableDetails", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableDetails " + tableInfo);
		return dbean;
	}

	public ArrayList<String> backupTableData(TableInfo tableInfo, LinkedHashMap<String, String> columnNames,
			String userId) throws DMWException {
		Logger.start(this, "backupTableData table Id:" + tableInfo);
		long la = System.currentTimeMillis();
		// TableInfo tableInfo = backupTableDatarmation(tableId);
		StringBuffer query = new StringBuffer(" SELECT 'INSERT INTO ");
		query.append(tableInfo.getOwner() + "." + tableInfo.getTableName() + " (");
		Collection<String> c = columnNames.keySet();
		Iterator<String> itr = c.iterator();
		while (itr.hasNext()) {
			String columnName = itr.next();
			if (!columnName.equalsIgnoreCase("RID"))
				query.append(columnName + " ,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append(") VALUES ('");
		itr = c.iterator();
		while (itr.hasNext()) {
			String columnName = itr.next();
			if (!columnName.equalsIgnoreCase("RID"))
				query.append("|| ''''	       || " + columnName + "	       || ''''	       || ','");
		}
		query.delete(query.length() - 6, query.length());
		query.append("|| ')' FROM ");
		query.append(tableInfo.getOwner() + "." + tableInfo.getTableName());
		ResultSet rs = null;
		PreparedStatement smt = null;

		ArrayList<String> al = new ArrayList<>();
		Connection con = null;
		Logger.info(this, "backupTableData", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "backupTableData", "Qry :" + query.toString(), null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query.toString());
			rs = smt.executeQuery();
			rs.setFetchSize(100);
			int k = 0;
			while (rs.next()) {
				al.add(rs.getString(1));
				// System.out.println(rs.getString(1));
				// audit.insertAuditLog("backupTableData", "Count of records : "
				// + k++, rs.getString(1), userId);
				audit.insertMessageLog("backupTableData", "Insert Query...", rs.getString(1), userId, null, null);
			}
			// dbean.setHeader(header);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "backupTableData", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "backupTableData()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "backupTableData", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "backupTableData " + tableInfo);
		return al;
	}

	public ArrayList<String> backupDeleteTableData(TableInfo tableInfo, LinkedHashMap<String, String> columnNames,
			String whereCond, String userId) throws DMWException {
		Logger.start(this, "backupDeleteTableData table Id:" + tableInfo);
		long la = System.currentTimeMillis();
		// TableInfo tableInfo = backupDeleteTableDatarmation(tableId);
		StringBuffer query = new StringBuffer(" SELECT 'INSERT INTO ");
		query.append(tableInfo.getOwner() + "." + tableInfo.getTableName() + " (");
		Collection<String> c = columnNames.keySet();
		Iterator<String> itr = c.iterator();
		while (itr.hasNext()) {
			String columnName = itr.next();
			if (!columnName.equalsIgnoreCase("RID"))
				query.append(columnName + " ,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append(") VALUES ('");
		itr = c.iterator();
		while (itr.hasNext()) {
			String columnName = itr.next();
			if (!columnName.equalsIgnoreCase("RID"))
				query.append("|| ''''	       || " + columnName + "	       || ''''	       || ','");
		}
		query.delete(query.length() - 6, query.length());
		query.append("|| ')' FROM ");
		query.append(tableInfo.getOwner() + "." + tableInfo.getTableName());
		query.append("  " + whereCond);
		ResultSet rs = null;
		PreparedStatement smt = null;

		ArrayList<String> al = new ArrayList<>();
		Connection con = null;
		Logger.info(this, "backupDeleteTableData", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "backupDeleteTableData", "Qry :" + query.toString(), null, null);
		// LinkedHashMap<String, String> headers = new LinkedHashMap<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query.toString());
			rs = smt.executeQuery();
			rs.setFetchSize(100);
			while (rs.next()) {
				al.add(rs.getString(1));
				audit.insertMessageLog("Delete Record", "backup of Deleted Record", rs.getString(1), userId, null,
						null);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "backupDeleteTableData", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "backupDeleteTableData()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "backupDeleteTableData", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "backupDeleteTableData " + tableInfo);
		return al;
	}

	public boolean getVersion(TableInfo tableInfo) throws DMWException {
		Logger.start(this, "getVersion table Id:" + tableInfo);
		long la = System.currentTimeMillis();
		// TableInfo tableInfo = getVersionrmation(tableId);
		StringBuffer query = new StringBuffer(" SELECT COUNT(*) FROM I2WEBDATA.DMW_TABLE_INFO WHERE DMW_TABLE_INFO_ID="
				+ tableInfo.getId() + " AND VERSION=" + tableInfo.getVersion());

		ResultSet rs = null;
		PreparedStatement smt = null;

		boolean bool = false;
		Connection con = null;
		Logger.info(this, "getVersion", "tableInfo:" + tableInfo, null, null);
		Logger.info(this, "getVersion", "Qry :" + query.toString(), null, null);
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query.toString());
			rs = smt.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0)
					bool = true;
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getVersion", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getVersion()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getVersion", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getVersion " + tableInfo);
		return bool;
	}

	public boolean changeVersion(int id, int version, String remoteUser) throws DMWException {
		Logger.start(this, "changeVersion table Id:" + id);
		long la = System.currentTimeMillis();
		// TableInfo tableInfo = changeVersionrmation(tableId);
		StringBuffer query = new StringBuffer(
				" UPDATE I2WEBDATA.DMW_TABLE_INFO SET VERSION=" + version + " WHERE DMW_TABLE_INFO_ID=" + id);

		ResultSet rs = null;
		PreparedStatement smt = null;

		boolean bool = false;
		Connection con = null;
		Logger.info(this, "changeVersion", "id:" + id, null, null);
		Logger.info(this, "changeVersion", "Qry :" + query.toString(), null, null);
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query.toString());
			int k = smt.executeUpdate();
			if (k > 0) {
				bool = true;
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "changeVersion", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "changeVersion()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "changeVersion", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "changeVersion " + id);
		return bool;
	}

	public String saveFavourites(String tableId, String jsonLine, String remoteUser) throws DMWException {
		Logger.start(this, "saveFavourites");
		long la = System.currentTimeMillis();
		// TableInfo tableInfo = changeVersionrmation(tableId);
		String deleteQry = Constants.DMW_FAVOURITES_DELETE_QUERY.replaceAll(":DMW_TABLE_INFO_ID", tableId)
				.replaceAll(":USER_NAME", remoteUser);
		String qry = Constants.DMW_FAVOURITES_SAVE_QUERY;
		ResultSet rs = null;
		PreparedStatement smt = null;

		String str = "failure";
		Connection con = null;
		Logger.info(this, "saveFavourites", "id:" + tableId, null, null);
		Logger.info(this, "saveFavourites", "Qry :" + qry, null, null);
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			con.setAutoCommit(false);
			smt = con.prepareStatement(deleteQry);
			smt.executeUpdate();
			smt.close();
			smt = con.prepareStatement(qry);
			if (jsonLine != null) {
				JsonParser json = new JsonParser();
				JsonArray jArr = (JsonArray) json.parse(jsonLine);
				for (int i = 0; i < jArr.size(); i++) {
					JsonObject jObj = new JsonObject();
					jObj = (JsonObject) jArr.get(i);

					smt.setString(1, tableId);
					smt.setString(2, remoteUser);
					smt.setString(3, (jObj.get("property").getAsString()));
					smt.setString(4, (jObj.get("operator").getAsString()));
					smt.setString(5, (jObj.get("value").getAsString()));
					smt.executeUpdate();
				}
			}
			con.commit();
			str = "success";
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "saveFavourites", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "saveFavourites()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.rollback();
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "saveFavourites", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "saveFavourites " + tableId);
		return str;
	}

	public List<SimpleComboBean> getEditableColumns(TableInfo tableInfo) throws DMWException {
		Logger.start(this, "getEditableColumns tableInfo:" + tableInfo);
		long la = System.currentTimeMillis();
		/*
		 * String qry =
		 * Constants.DMW_EDITABLE_COLUMNS_QUERY.replaceAll(":TABLE_ID",
		 * tableInfo.getId() + "") .replaceAll(":TABLE_NAME",
		 * tableInfo.getTableName() + "") .replaceAll(":TABLE_OWNER",
		 * tableInfo.getOwner() + "");
		 */
		String qry = "SELECT EDITABLE  FROM i2webdata.dmw_table_access WHERE DMW_TABLE_INFO_ID = " + tableInfo.getId();
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getEditableColumns", "tableId:" + tableInfo.getId(), null, null);
		Logger.info(this, "getEditableColumns", "Qry :" + qry, null, null);
		List<SimpleComboBean> beanList = new ArrayList<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			int k = 0;
			if (tableInfo.isPivot()) {
				qry = "SELECT Distinct " + tableInfo.getPivotColumns() + " FROM " + tableInfo.getOwner() + "."
						+ tableInfo.getTableName() + " order by 1";
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				while (rs.next()) {
					SimpleComboBean bean = new SimpleComboBean();
					bean.setName(rs.getString(1));
					bean.setId(k++);
					beanList.add(bean);
				}
			} else {
				smt = con.prepareStatement(qry);
				rs = smt.executeQuery();
				String str = "";
				if (rs.next()) {
					str = rs.getString(1);
				}

				if (str.equalsIgnoreCase("Y")) {
					qry = "SELECT DISTINCT COLUMN_NAME  FROM ALL_TAB_COLUMNS"
							+ " WHERE     UPPER (table_name) = UPPER ('" + tableInfo.getTableName() + "')"
							+ "       AND UPPER (owner) = UPPER ('" + tableInfo.getOwner() + "')" + "       order by 1";
					Logger.info(this, "getEditableColumns", "Qry :" + qry, null, null);
					smt.close();
					smt = con.prepareStatement(qry);
					rs.close();
					rs = smt.executeQuery();
				} else {
					qry = "SELECT COLUMN_NAME            FROM i2webdata.DMW_TABLE_EDITABLE"
							+ "           WHERE DMW_TABLE_INFO_ID = " + tableInfo.getId();
					Logger.info(this, "getEditableColumns", "Qry :" + qry, null, null);
					smt.close();
					smt = con.prepareStatement(qry);
					rs.close();
					rs = smt.executeQuery();
				}
				while (rs.next()) {
					SimpleComboBean bean = new SimpleComboBean();
					bean.setName(rs.getString("COLUMN_NAME"));
					bean.setId(k++);
					beanList.add(bean);
				}

			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getEditableColumns", "Sql exception", null, se);
		} catch (DMWException e) {
			Logger.error(this, "getEditableColumns()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getEditableColumns", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getEditableColumns " + beanList);

		return beanList;
	}

	public ArrayList<String> getOldValues(String query, String userId) throws DMWException {
		Logger.start(this, "getOldValues userId:" + userId);
		long la = System.currentTimeMillis();
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getOldValues", "userId:" + userId, null, null);
		Logger.info(this, "getOldValues", "Qry :" + query, null, null);
		ArrayList<String> al = new ArrayList<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query);
			rs = smt.executeQuery();
			int k = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= k; i++)
					al.add(rs.getString(i));
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getOldValues", "Sql exception", null, se);
			throw new DMWException("Internal server error has occured.");

		} catch (DMWException e) {
			Logger.error(this, "getOldValues()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getOldValues", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getOldValues userId:" + userId);
		return al;
	}

	public HashMap<String, String> getSystemValues(int tableId, String userId) throws DMWException {
		Logger.start(this, "getSystemValues userId:" + userId);
		long la = System.currentTimeMillis();
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		String query = Constants.DMW_SYSTEM_VALUES_QUERY.replaceAll(":DMW_TABLE_INFO_ID", tableId + "");
		Logger.info(this, "getSystemValues", "userId:" + userId, null, null);
		Logger.info(this, "getSystemValues", "Qry :" + query, null, null);
		HashMap<String, String> al = new HashMap<String, String>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query);
			rs = smt.executeQuery();
			while (rs.next()) {
				if (rs.getString("COLUMN_NAME") != null && !rs.getString("COLUMN_NAME").equalsIgnoreCase(""))
					al.put(rs.getString("AUTO_FIELD"), rs.getString("COLUMN_NAME"));
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getSystemValues", "Sql exception", null, se);
			throw new DMWException("Internal server error has occured.");

		} catch (DMWException e) {
			Logger.error(this, "getSystemValues()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getSystemValues", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getSystemValues userId:" + userId);
		return al;
	}

	public ArrayList<String> getPrimaryKeyValues(String tableName, String tableOwner, String userId)
			throws DMWException {
		Logger.start(this, "getPrimaryKeyValues userId:" + userId);
		long la = System.currentTimeMillis();
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		String query = Constants.DMW_PRIMARY_KEY_VALUES_QUERY.replaceAll(":TABLE_NAME", tableName + "")
				.replaceAll(":TABLE_OWNER", tableOwner + "");
		Logger.info(this, "getPrimaryKeyValues", "userId:" + userId, null, null);
		Logger.info(this, "getPrimaryKeyValues", "Qry :" + query, null, null);
		ArrayList<String> al = new ArrayList<String>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(query);
			rs = smt.executeQuery();
			while (rs.next()) {
				al.add(rs.getString(1));
			}

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getPrimaryKeyValues", "Sql exception", null, se);
			throw new DMWException("Internal server error has occured.");

		} catch (DMWException e) {
			Logger.error(this, "getPrimaryKeyValues()", "DMWException", null, e);
			throw e;
		} finally {
			// Always make sure r(rs.getString("ult sets and statements are
			// closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (smt != null) {
				try {
					smt.close();
				} catch (SQLException e) {
					;
				}
				smt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
				con = null;
			}
		}

		long lb = System.currentTimeMillis();
		Logger.info(this, "getPrimaryKeyValues", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getPrimaryKeyValues userId:" + userId);
		return al;
	}
}
