package com.mxim.dmw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mxim.dmw.domain.SimpleComboBean;
import com.mxim.dmw.domain.TableAccess;
import com.mxim.dmw.domain.TableInfo;
import com.mxim.dmw.exception.DMWException;
import com.mxim.dmw.util.ConnectionUtil;
import com.mxim.dmw.util.Constants;
import com.mxim.dmw.util.Logger;
/**Deprecated*/
public class DMWAdminDao {
	DMWAuditDao audit = new DMWAuditDao();

	public ArrayList<SimpleComboBean> getRolesList(String userId) throws DMWException {
		Logger.start(this, "getRolesList userId:" + userId);
		long la = System.currentTimeMillis();
		audit.insertAuditLog("getRolesList", "Start of the getRolesList", Constants.DMW_ROLES_QUERY, userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getRolesList", "userId:" + userId, null, null);
		Logger.info(this, "getRolesList", "Qry :" + Constants.DMW_ROLES_QUERY, null, null);
		audit.insertAuditLog("getRolesList", "Getting the getRolesList in admin", Constants.DMW_ROLES_QUERY, userId);
		ArrayList<SimpleComboBean> al = new ArrayList<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(Constants.DMW_ROLES_QUERY);
			rs = smt.executeQuery();
			while (rs.next()) {
				SimpleComboBean bean = new SimpleComboBean();
				bean.setName(rs.getString("ROLE_NAME"));
				bean.setId(rs.getInt("DMW_ROLE_ID"));
				al.add(bean);
			}
			audit.insertAuditLog("getRolesList", "Got the getRolesList Admin", Constants.DMW_ROLES_QUERY, userId);

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getRolesList", "Sql exception", null, se);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "getRolesList()", "DMWException", null, e);
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
		Logger.info(this, "getRolesList", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getRolesList userId:" + userId);
		audit.insertAuditLog("getRolesList", "End of the getRolesList Admin", Constants.DMW_ROLES_QUERY, userId);
		return al;
	}

	public ArrayList<SimpleComboBean> getTableOwners(String userId) throws DMWException {
		Logger.start(this, "getTableOwners userId:" + userId);
		long la = System.currentTimeMillis();
		audit.insertAuditLog("getTableOwners", "Start of the getTableOwners", Constants.DMW_ALL_OWNERS_QUERY, userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getTableOwners", "userId:" + userId, null, null);
		Logger.info(this, "getTableOwners", "Qry :" + Constants.DMW_ALL_OWNERS_QUERY, null, null);
		audit.insertAuditLog("getTableOwners", "Getting the getTableOwners in admin", Constants.DMW_ALL_OWNERS_QUERY,
				userId);
		ArrayList<SimpleComboBean> al = new ArrayList<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(Constants.DMW_ALL_OWNERS_QUERY);
			rs = smt.executeQuery();
			while (rs.next()) {
				SimpleComboBean bean = new SimpleComboBean();
				bean.setName(rs.getString("OWNER"));
				bean.setId(rs.getInt("rowum"));
				al.add(bean);
			}
			audit.insertAuditLog("getTableOwners", "Got the getTableOwners Admin", Constants.DMW_ALL_OWNERS_QUERY,
					userId);

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableOwners", "Sql exception", null, se);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "getTableOwners()", "DMWException", null, e);
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
		Logger.info(this, "getTableOwners", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableOwners userId:" + userId);
		audit.insertAuditLog("getTableOwners", "End of the getRolesList Admin", Constants.DMW_ALL_OWNERS_QUERY, userId);
		return al;
	}

	public ArrayList<SimpleComboBean> getTablesList(String owner, String tableName, String userId) throws DMWException {
		Logger.start(this, "getTablesList userId:" + userId);
		long la = System.currentTimeMillis();
		audit.insertAuditLog("getTablesList", "Start of the getTablesList",
				Constants.DMW_ALL_TABLES_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getTablesList", "userId:" + userId, null, null);
		Logger.info(this, "getTablesList", "Qry :"
				+ Constants.DMW_ALL_TABLES_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName), null,
				null);
		audit.insertAuditLog("getTablesList", "Getting the getTablesList in admin",
				Constants.DMW_ALL_TABLES_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				userId);
		ArrayList<SimpleComboBean> al = new ArrayList<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(
					Constants.DMW_ALL_TABLES_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName));
			rs = smt.executeQuery();
			while (rs.next()) {
				SimpleComboBean bean = new SimpleComboBean();
				bean.setName(rs.getString("TABLE_NAME"));
				bean.setId(rs.getInt("rowum"));
				al.add(bean);
			}
			audit.insertAuditLog("getTablesList", "Got the getTablesList Admin",
					Constants.DMW_ALL_TABLES_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
					userId);

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTablesList", "Sql exception", null, se);
			audit.insertAuditLog("DMWException", "ORA-" + se.getErrorCode(), se.getMessage(), userId);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "getTablesList()", "DMWException", null, e);
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
		Logger.info(this, "getTablesList", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTablesList userId:" + userId);
		audit.insertAuditLog("getTablesList", "End of the getTablesList Admin",
				Constants.DMW_ALL_TABLES_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				userId);
		return al;
	}

	public int getTableSequence(String userId) throws DMWException {
		Logger.start(this, "getTableSequence ");
		long la = System.currentTimeMillis();
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getTableSequence", "Qry :" + Constants.DMW_TABLES_SEQ_QUERY, null, null);
		int seq = -1;
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(Constants.DMW_TABLES_SEQ_QUERY);
			rs = smt.executeQuery();
			if (rs.next()) {
				seq = rs.getInt(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getTableSequence", "Sql exception", null, se);
			audit.insertAuditLog("DMWException", "ORA-" + se.getErrorCode(), se.getMessage(), userId);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "getTableSequence()", "DMWException", null, e);
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
		Logger.info(this, "getTableSequence", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getTableSequence ");
		return seq;
	}

	public boolean saveNewTableInfo(TableInfo info, ArrayList<TableAccess> tableAccessList, String userId)
			throws DMWException {
		Logger.start(this, "saveNewTableInfo userId:" + userId);
		long la = System.currentTimeMillis();
		audit.insertAuditLog("saveNewTableInfo", "Start of the saveNewTableInfo", "", userId);
		int tableId = getTableSequence(userId);
		String query = Constants.DMW_TABLES_INSERT_QUERY.replaceAll(":DMW_TABLE_INFO_ID", tableId + "")
				.replaceAll(":TABLE_NAME", info.getTableName()).replaceAll(":DMW_TABLE_CONF_ID", info.getId() + "")
				.replaceAll(":TABLE_OWNER", info.getOwner()).replaceAll(":SCHEMA_NAME", info.getSchemaName())
				.replaceAll(":DMW_ROLE", info.isRole() ? "N" : "Y").replaceAll(":TABLE_ALIAS", info.getTableAlias())
				.replaceAll(":CONDITIONAL_QUERY", info.isCondQuery() ? "Y" : "N")
				.replaceAll(":PIVOT", info.isPivot() ? "Y" : "N").replaceAll(":PAGINATION", info.isPaging() ? "Y" : "N")
				.replaceAll(":EDITABLE", info.isEditable() ? "Y" : "N");

		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "saveNewTableInfo", "userId:" + userId, null, null);
		Logger.info(this, "saveNewTableInfo", "Qry :" + query, null, null);
		audit.insertAuditLog("saveNewTableInfo", "Framing the saveNewTableInfo Query", query, userId);
		boolean bool = false;
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			con.setAutoCommit(false);
			smt = con.prepareStatement(query);
			smt.executeUpdate();
			smt.close();
			if (!info.isRole()) {
				query = Constants.DMW_TABLES_ROLES_INSERT_QUERY;
				if (info.getRoles() != null && !info.getRoles().equalsIgnoreCase("")) {
					String[] role = info.getRoles().split(",");
					for (int i = 0; i < role.length; i++) {
						smt = con.prepareStatement(query);
						smt.setInt(1, tableId);
						smt.setInt(2, Integer.parseInt(role[i]));
						audit.insertAuditLog("saveNewTableInfo", "Inserting Roles values", smt.toString(), userId);
						Logger.info(this, "saveNewTableInfo", "Qry :" + query, null, null);
						smt.executeUpdate();
					}
				}
				audit.insertAuditLog("saveNewTableInfo", "Inserted Roles Suucessfully", query, userId);
				query = Constants.DMW_TABLE_ACCESS_INSERT_QUERY;
				smt = con.prepareStatement(query);
				for (TableAccess access : tableAccessList) {
					smt.setInt(1, tableId);
					smt.setString(2, access.isAddRow() ? "Y" : "N");
					smt.setString(3, access.isDelRow() ? "Y" : "N");
					smt.setString(4, access.isMassUpdate() ? "Y" : "N");
					smt.setString(5, access.isUpload() ? "Y" : "N");
					smt.setString(6, access.isDownload() ? "Y" : "N");
					smt.setInt(7, access.getRoleId());
					smt.setString(8, access.isEditable() ? "Y" : "N");
					// audit.insertAuditLog("saveNewTableInfo", "Inserting Table
					// Access values", smt.toString(), userId);
					if (smt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
						String x = ((oracle.jdbc.driver.OraclePreparedStatement) smt).getOriginalSql();
						audit.insertAuditLog("saveNewTableInfo", "Inserting  Table Access values", x, userId);
						Logger.info(this, "saveNewTableInfo", "Qry :" + x, null, null);
					} else
						audit.insertAuditLog("saveNewTableInfo", "Inserting  Table Access values", smt.toString(),
								userId);
					smt.executeUpdate();
					if (!access.isEditable()) {
						String qry = Constants.DMW_TABLE_EDITABLE_INSERT_QUERY;
						PreparedStatement psmt = con.prepareStatement(qry);
						for (String colName : access.getEditableColumns()) {
							psmt.setInt(1, tableId);
							psmt.setString(2, colName.replaceAll("\"", ""));
							psmt.setString(3, "Y");
							psmt.setInt(4, access.getRoleId());
							if (psmt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
								String x = ((oracle.jdbc.driver.OraclePreparedStatement) psmt).getOriginalSql();
								audit.insertAuditLog("saveNewTableInfo", "Inserting  Table Edit Columns values", x,
										userId);
								Logger.info(this, "saveNewTableInfo", "Qry :" + x, null, null);
							} else
								audit.insertAuditLog("saveNewTableInfo", "Inserting  Table Edit Columns values",
										psmt.toString(), userId);
							if (!colName.replaceAll("\"", "").equalsIgnoreCase(""))
								psmt.executeUpdate();
						}
					}
				}

			}
			if (info.isCondQuery()) {
				query = Constants.DMW_TABLES_QUERY_INSERT_QUERY;
				smt = con.prepareStatement(query);
				smt.setInt(1, tableId);
				smt.setString(2, info.getQuery());
				if (smt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
					String x = ((oracle.jdbc.driver.OraclePreparedStatement) smt).getOriginalSql();
					audit.insertAuditLog("saveNewTableInfo", "Inserting Conditional Query values", x, userId);
				} else
					audit.insertAuditLog("saveNewTableInfo", "Inserting Conditional Query values", smt.toString(),
							userId);
				smt.executeUpdate();
			}
			if (info.isPaging()) {
				query = Constants.DMW_TABLES_PAGING_INSERT_QUERY;
				smt = con.prepareStatement(query);
				smt.setInt(1, tableId);
				smt.setInt(2, info.getPageLength());
				smt.setString(3, info.getSortBy());
				smt.setString(4, info.getSortDir());
				if (smt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
					String x = ((oracle.jdbc.driver.OraclePreparedStatement) smt).getOriginalSql();
					audit.insertAuditLog("saveNewTableInfo", "Inserting Pagination values", x, userId);
				} else
					audit.insertAuditLog("saveNewTableInfo", "Inserting Pagination values", smt.toString(), userId);
				smt.executeUpdate();
			}
			if (info.isPivot()) {
				query = Constants.DMW_TABLES_PIVOT_INSERT_QUERY;
				smt = con.prepareStatement(query);
				smt.setInt(1, tableId);
				smt.setString(2, info.getFixedFields());
				smt.setString(3, info.getPivotColumns());
				smt.setString(4, info.getPivotData());
				if (smt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
					String x = ((oracle.jdbc.driver.OraclePreparedStatement) smt).getOriginalSql();
					audit.insertAuditLog("saveNewTableInfo", "Inserting Pivot values", x, userId);
				} else
					audit.insertAuditLog("saveNewTableInfo", "Inserting Pivot values", smt.toString(), userId);
				smt.executeUpdate();
			}
			if (info.getSystemUser() != null || info.getSystemUser().equalsIgnoreCase("")) {
				query = Constants.DMW_TABLES_AUTO_INSERT_QUERY;
				smt = con.prepareStatement(query);
				smt.setInt(1, tableId);
				smt.setString(2, info.getSystemUser());
				smt.setString(3, "SYSTEM_USER");
				if (smt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
					String x = ((oracle.jdbc.driver.OraclePreparedStatement) smt).getOriginalSql();
					audit.insertAuditLog("saveNewTableInfo", "Inserting Auto values", x, userId);
				} else
					audit.insertAuditLog("saveNewTableInfo", "Inserting Auto values", smt.toString(), userId);
				smt.executeUpdate();
			}
			if (info.getSystemDate() != null || info.getSystemDate().equalsIgnoreCase("")) {
				query = Constants.DMW_TABLES_AUTO_INSERT_QUERY;
				smt = con.prepareStatement(query);
				smt.setInt(1, tableId);
				smt.setString(2, info.getSystemDate());
				smt.setString(3, "SYSTEM_DATE");
				if (smt instanceof oracle.jdbc.driver.OraclePreparedStatement) {
					String x = ((oracle.jdbc.driver.OraclePreparedStatement) smt).getOriginalSql();
					audit.insertAuditLog("saveNewTableInfo", "Inserting Auto values", x, userId);
				} else
					audit.insertAuditLog("saveNewTableInfo", "Inserting Auto values", smt.toString(), userId);
				smt.executeUpdate();
			}

			con.commit();
			bool = true;
			audit.insertAuditLog("saveNewTableInfo", "Inserted Suucessfully", query, userId);

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "saveNewTableInfo", "Sql exception", null, se);
			audit.insertAuditLog("DMWException", "ORA-" + se.getErrorCode(), se.getMessage(), userId);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "saveNewTableInfo()", "DMWException", null, e);
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
		Logger.info(this, "saveNewTableInfo", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "saveNewTableInfo userId:" + userId);
		audit.insertAuditLog("saveNewTableInfo", "End of the saveNewTableInfo Admin", query, userId);
		return bool;
	}

	public ArrayList<SimpleComboBean> getColumnsList(String owner, String tableName, String userId)
			throws DMWException {
		Logger.start(this, "getColumnsList userId:" + userId);
		long la = System.currentTimeMillis();
		audit.insertAuditLog("getColumnsList", "Start of the getColumnsList",
				Constants.DMW_ALL_COLUMNS_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				userId);
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		Logger.info(this, "getColumnsList", "userId:" + userId, null, null);
		Logger.info(this, "getColumnsList", "Qry :"
				+ Constants.DMW_ALL_COLUMNS_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				null, null);
		audit.insertAuditLog("getColumnsList", "Getting the getColumnsList in admin",
				Constants.DMW_ALL_COLUMNS_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				userId);
		ArrayList<SimpleComboBean> al = new ArrayList<>();
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(
					Constants.DMW_ALL_COLUMNS_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName));
			rs = smt.executeQuery();
			while (rs.next()) {
				SimpleComboBean bean = new SimpleComboBean();
				bean.setName(rs.getString("COLUMN_NAME"));
				bean.setId(rs.getInt("COLUMN_ID"));
				al.add(bean);
			}
			audit.insertAuditLog("getColumnsList", "Got the getColumnsList Admin",
					Constants.DMW_ALL_COLUMNS_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
					userId);

		} catch (SQLException se) {
			se.printStackTrace();
			Logger.error(this, "getColumnsList", "Sql exception", null, se);
			audit.insertAuditLog("DMWException", "ORA-" + se.getErrorCode(), se.getMessage(), userId);
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
			Logger.error(this, "getColumnsList()", "DMWException", null, e);
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
		Logger.info(this, "getColumnsList", "time Taken :" + (lb - la), null, null);
		Logger.end(this, "getColumnsList userId:" + userId);
		audit.insertAuditLog("getColumnsList", "End of the getTablesList Admin",
				Constants.DMW_ALL_COLUMNS_QUERY.replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				userId);
		return al;
	}
}
