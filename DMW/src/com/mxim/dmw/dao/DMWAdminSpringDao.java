package com.mxim.dmw.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.mxim.dmw.domain.SimpleComboBean;
import com.mxim.dmw.domain.TableAccess;
import com.mxim.dmw.domain.TableInfo;
import com.mxim.dmw.exception.DMWException;

public class DMWAdminSpringDao {
	private DMWAuditSpringDao auditDao;

	public DMWAuditSpringDao getAuditDao() {
		return auditDao;
	}

	public void setAuditDao(DMWAuditSpringDao auditDao) {
		this.auditDao = auditDao;
	}

	public ParameterizedRowMapper<SimpleComboBean> getRequestComboMapper() {
		return requestComboMapper;
	}

	public void setRequestComboMapper(ParameterizedRowMapper<SimpleComboBean> requestComboMapper) {
		this.requestComboMapper = requestComboMapper;
	}

	private PlatformTransactionManager transactionManager;
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

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public List<SimpleComboBean> getRolesList(String userId) throws DMWException {
		auditDao.insertAuditLog("getRolesList", "Getting the Roles", SQLMap.get("DMW_ROLES_QUERY"), userId);
		return simpleJdbcTemplate.query(SQLMap.get("DMW_ROLES_QUERY"), requestComboMapper);
	}

	ParameterizedRowMapper<SimpleComboBean> requestComboMapper = new ParameterizedRowMapper<SimpleComboBean>() {

		public SimpleComboBean mapRow(ResultSet rs, int i) throws SQLException {
			SimpleComboBean bean = new SimpleComboBean();
			bean.setName(rs.getString("NAME"));
			bean.setId(rs.getInt("ID"));
			return bean;
		}

	};

	public List<SimpleComboBean> getTableOwners(String remoteUser) throws DMWException {
		auditDao.insertAuditLog("getTableOwners", "getting the All Schemas", SQLMap.get("DMW_ALL_OWNERS_QUERY"),
				remoteUser);
		return simpleJdbcTemplate.query(SQLMap.get("DMW_ALL_OWNERS_QUERY"), requestComboMapper);
	}

	public List<SimpleComboBean> getTablesList(String owner, String tableName, String remoteUser) throws DMWException {
		// System.out.println("Seq
		// **************************************************
		// "+getTableSequence(remoteUser));
		auditDao.insertAuditLog("getTablesList", "getting the All Tables",
				SQLMap.get("DMW_ALL_TABLES_QUERY").replaceAll(":OWNER", owner), remoteUser);
		return simpleJdbcTemplate.query(SQLMap.get("DMW_ALL_TABLES_QUERY").replaceAll(":OWNER", owner),
				requestComboMapper);
	}

	public List<SimpleComboBean> getColumnsList(String owner, String tableName, String remoteUser) throws DMWException {
		auditDao.insertAuditLog("getColumnsList", "getting the All Tables",
				SQLMap.get("DMW_ALL_COLUMNS_QUERY").replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				remoteUser);
		return simpleJdbcTemplate.query(
				SQLMap.get("DMW_ALL_COLUMNS_QUERY").replaceAll(":OWNER", owner).replaceAll(":TABLE_NAME", tableName),
				requestComboMapper);
	}

	public int getTableSequence(String userId) throws DMWException {
		auditDao.insertAuditLog("getTableSequence", "Getting the Roles", SQLMap.get("DMW_TABLES_SEQ_QUERY"), userId);
		return simpleJdbcTemplate.queryForInt(SQLMap.get("DMW_TABLES_SEQ_QUERY"), new HashMap<>());

	}

	public boolean saveNewTableInfo(TableInfo info, ArrayList<TableAccess> tableAccessList, String userId)
			throws DMWException {
		boolean bool = false;
		int tableId = getTableSequence(userId);
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		try {
			auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into DMW_TABLE_INFO", SQLMap
					.get("DMW_TABLES_INSERT_QUERY").replaceAll(":DMW_TABLE_INFO_ID", tableId + "")
					.replaceAll(":TABLE_NAME", info.getTableName()).replaceAll(":DMW_TABLE_CONF_ID", info.getId() + "")
					.replaceAll(":TABLE_OWNER", info.getOwner()).replaceAll(":SCHEMA_NAME", info.getSchemaName())
					.replaceAll(":DMW_ROLE", info.isRole() ? "N" : "Y").replaceAll(":TABLE_ALIAS", info.getTableAlias())
					.replaceAll(":CONDITIONAL_QUERY", info.isCondQuery() ? "Y" : "N")
					.replaceAll(":PIVOT", info.isPivot() ? "Y" : "N")
					.replaceAll(":PAGINATION", info.isPaging() ? "Y" : "N")
					.replaceAll(":EDITABLE", info.isEditable() ? "Y" : "N"), userId);
			simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_INSERT_QUERY")
					.replaceAll(":DMW_TABLE_INFO_ID", tableId + "").replaceAll(":TABLE_NAME", info.getTableName())
					.replaceAll(":DMW_TABLE_CONF_ID", info.getId() + "").replaceAll(":TABLE_OWNER", info.getOwner())
					.replaceAll(":SCHEMA_NAME", info.getSchemaName()).replaceAll(":DMW_ROLE", info.isRole() ? "N" : "Y")
					.replaceAll(":TABLE_ALIAS", info.getTableAlias())
					.replaceAll(":CONDITIONAL_QUERY", info.isCondQuery() ? "Y" : "N")
					.replaceAll(":PIVOT", info.isPivot() ? "Y" : "N")
					.replaceAll(":PAGINATION", info.isPaging() ? "Y" : "N")
					.replaceAll(":EDITABLE", info.isEditable() ? "Y" : "N"), new HashMap<String, String>());
			auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting data into DMW_TABLE_INFO", SQLMap
					.get("DMW_TABLES_INSERT_QUERY").replaceAll(":DMW_TABLE_INFO_ID", tableId + "")
					.replaceAll(":TABLE_NAME", info.getTableName()).replaceAll(":DMW_TABLE_CONF_ID", info.getId() + "")
					.replaceAll(":TABLE_OWNER", info.getOwner()).replaceAll(":SCHEMA_NAME", info.getSchemaName())
					.replaceAll(":DMW_ROLE", info.isRole() ? "N" : "Y").replaceAll(":TABLE_ALIAS", info.getTableAlias())
					.replaceAll(":CONDITIONAL_QUERY", info.isCondQuery() ? "Y" : "N")
					.replaceAll(":PIVOT", info.isPivot() ? "Y" : "N")
					.replaceAll(":PAGINATION", info.isPaging() ? "Y" : "N")
					.replaceAll(":EDITABLE", info.isEditable() ? "Y" : "N"), userId);

			if (info.getRoles() != null && !info.getRoles().equalsIgnoreCase("")) {
				String[] role = info.getRoles().split(",");
				for (int i = 0; i < role.length; i++) {

					auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into Roles Table",
							SQLMap.get("DMW_TABLES_ROLES_INSERT_QUERY"), userId);
					simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_ROLES_INSERT_QUERY"), tableId,
							Integer.parseInt(role[i]));
					auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into Roles Table",
							SQLMap.get("DMW_TABLES_ROLES_INSERT_QUERY"), userId);
				}
			}
			for (TableAccess access : tableAccessList) {
				auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into DMW_TABLE_ACCESS",
						SQLMap.get("DMW_TABLE_ACCESS_INSERT_QUERY"), userId);
				simpleJdbcTemplate.update(SQLMap.get("DMW_TABLE_ACCESS_INSERT_QUERY"), tableId,
						access.isAddRow() ? "Y" : "N", access.isDelRow() ? "Y" : "N", access.isMassUpdate() ? "Y" : "N",
						access.isUpload() ? "Y" : "N", access.isDownload() ? "Y" : "N", access.getRoleId(),
						access.isEditable() ? "Y" : "N");
				auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into DMW_TABLE_ACCESS",
						SQLMap.get("DMW_TABLE_ACCESS_INSERT_QUERY"), userId);
				if (!access.isEditable()) {
					for (String colName : access.getEditableColumns()) {
						if (colName != null && !colName.equalsIgnoreCase("")) {
							auditDao.insertAuditLog("saveNewTableInfo",
									"Started Inserting into DMW_TABLE_EDITABLE_INSERT_QUERY",
									SQLMap.get("DMW_TABLE_EDITABLE_INSERT_QUERY"), userId);
							simpleJdbcTemplate.update(SQLMap.get("DMW_TABLE_EDITABLE_INSERT_QUERY"), tableId,
									colName.replaceAll("\"", ""), "Y", access.getRoleId());
							auditDao.insertAuditLog("saveNewTableInfo",
									"Done with Inserting Data into DMW_TABLE_EDITABLE_INSERT_QUERY",
									SQLMap.get("DMW_TABLE_EDITABLE_INSERT_QUERY"), userId);
						}
					}
				}
			}
			if (info.isCondQuery()) {

				auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into Conditinal Query Table",
						SQLMap.get("DMW_TABLES_QUERY_INSERT_QUERY"), userId);
				simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_QUERY_INSERT_QUERY"), tableId,
						info.getQuery().replaceAll("\"", ""));
				auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into Conditinal Query Table",
						SQLMap.get("DMW_TABLES_QUERY_INSERT_QUERY"), userId);

			}
			if (info.isPaging()) {
				auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into Paging Table",
						SQLMap.get("DMW_TABLES_PAGING_INSERT_QUERY"), userId);
				simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_PAGING_INSERT_QUERY"), tableId, info.getPageLength(),
						info.getSortBy(), info.getSortDir());
				auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into Paging Table",
						SQLMap.get("DMW_TABLES_PAGING_INSERT_QUERY"), userId);
			}
			if (info.isPivot()) {

				auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into Pivot Table",
						SQLMap.get("DMW_TABLES_PIVOT_INSERT_QUERY"), userId);
				simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_PIVOT_INSERT_QUERY"), tableId, info.getFixedFields(),
						info.getPivotColumns(), info.getPivotData());
				auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into Pivot Table",
						SQLMap.get("DMW_TABLES_PIVOT_INSERT_QUERY"), userId);

			}
			if (info.getSystemUser() != null || info.getSystemUser().equalsIgnoreCase("")) {

				auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into System Columns Table",
						SQLMap.get("DMW_TABLES_AUTO_INSERT_QUERY"), userId);
				simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_AUTO_INSERT_QUERY"), tableId, info.getSystemUser(),
						"SYSTEM_USER");
				auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into System Columns Table",
						SQLMap.get("DMW_TABLES_AUTO_INSERT_QUERY"), userId);
			}
			if (info.getSystemDate() != null || info.getSystemDate().equalsIgnoreCase("")) {
				auditDao.insertAuditLog("saveNewTableInfo", "Started Inserting into System Columns Table",
						SQLMap.get("DMW_TABLES_AUTO_INSERT_QUERY"), userId);
				simpleJdbcTemplate.update(SQLMap.get("DMW_TABLES_AUTO_INSERT_QUERY"), tableId, info.getSystemDate(),
						"SYSTEM_DATE");
				auditDao.insertAuditLog("saveNewTableInfo", "Done with Inserting Data into System Columns Table",
						SQLMap.get("DMW_TABLES_AUTO_INSERT_QUERY"), userId);

			}
			transactionManager.commit(status);
			bool = true;
		} catch (DataAccessException e) {
			System.out.println("Error in creating record, rolling back");
			transactionManager.rollback(status);
			throw e;
		}
		return bool;

	}
}