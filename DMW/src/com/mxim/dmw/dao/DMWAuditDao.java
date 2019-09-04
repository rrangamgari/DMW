package com.mxim.dmw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mxim.dmw.exception.DMWException;
import com.mxim.dmw.util.ConnectionUtil;

public class DMWAuditDao {
	public void insertAuditLog(String eventName, String eventDesc, String eventQuery, String userId)
			throws DMWException {
		String qry = "INSERT INTO I2WEBDATA.DMW_AUDIT_TRAIL (EVENT_NAME,"
				+ "                                       EVENT_DESCRIPTION,"
				+ "                                       EVENT_QUERY,"
				+ "                                       UPDATED_BY,"
				+ "                                       UPDATED_DATE)" + "     VALUES (?, ?,?,?, SYSDATE)";
		ResultSet rs = null;
		PreparedStatement smt = null;
		Connection con = null;
		try {
			con = ConnectionUtil.getConnection("jdbc/mppc");
			smt = con.prepareStatement(qry);
			smt.setString(1, eventName);
			smt.setString(2, eventDesc);
			smt.setString(3, eventQuery);
			smt.setString(4, userId);
			smt.executeUpdate();

		} catch (SQLException se) {
			se.printStackTrace();
			throw new DMWException("Internal server error has occured.");
		} catch (DMWException e) {
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

	}
}
