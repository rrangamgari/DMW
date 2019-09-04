package com.mxim.dmw.dao;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.mxim.dmw.exception.DMWException;

public class DMWAuditSpringDao {
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private Map<String, String> SQLMap;

	public Map<String, String> getSQLMap() {
		return SQLMap;
	}

	public void setSQLMap(Map<String, String> sQLMap) {
		SQLMap = sQLMap;
	}

	public void setDataSource(DataSource dataSource) throws SQLException {
		System.out.println(dataSource);
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}

	public void insertAuditLog(String eventName, String eventDesc, String eventQuery, String userId)
			throws DMWException {
		simpleJdbcTemplate.update(SQLMap.get("DMW_AUDIT_QUERY"), eventName, eventDesc, eventQuery, userId);
	}
	
}
