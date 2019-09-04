package com.mxim.dmw.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mxim.dmw.exception.DMWException;

/**
 * @author Ravinder.Rangamgari
 *
 */
public class ConnectionUtil {
	public static Connection getConnection(String dbName) throws DMWException{
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:comp/env");
			DataSource ds = (DataSource) envContext.lookup(dbName);
			Connection con = ds.getConnection();
			return con;
		} catch (NamingException ne) {
			ne.printStackTrace();
			System.out.println("ne --> " + ne);
		} catch (SQLException sqlE) {
			System.out.println("DbHelper , sqlE --> " + sqlE);
		}

		return null;
	}

	public static boolean closeAll(Connection con, ResultSet rs, PreparedStatement smt) {

		// Always make sure result sets and statements are closed,
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
		Logger.info(new ConnectionUtil(), "Close Connections", "All Connections are closed", "", null);
		return true;
	}

	public static boolean closeAll(Connection con, ResultSet rs, Statement smt) {

		// Always make sure result sets and statements are closed,
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
		Logger.info(new ConnectionUtil(), "Close Connections", "All Connections are closed", "", null);
		return true;
	}
}
