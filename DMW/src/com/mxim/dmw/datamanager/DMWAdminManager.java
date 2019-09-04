package com.mxim.dmw.datamanager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mxim.dmw.dao.DMWAdminSpringDao;
import com.mxim.dmw.dao.DMWDao;
import com.mxim.dmw.domain.SimpleComboBean;
import com.mxim.dmw.domain.TableAccess;
import com.mxim.dmw.domain.TableInfo;
import com.mxim.dmw.domain.TableList;
import com.mxim.dmw.exception.DMWException;
import com.mxim.dmw.util.Logger;

public class DMWAdminManager {
	public DMWAdminManager() {
		if (springDao == null) {
			initDMWDao();
		}
	}

	private DMWAdminSpringDao springDao;

	private void initDMWDao() {
		ApplicationContext ac = WebApplicationContextUtils
				.getWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
						.getRequest().getSession().getServletContext());
		this.springDao = (DMWAdminSpringDao) ac.getBean("DMWAdminSpringDao");
		// return dao;
	}

	public ArrayList<SimpleComboBean> getConfList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getConfList");
		DMWDao dao = new DMWDao();
		// ArrayList<TableList> tablelist =
		// dao.getParentNameList(request.getRemoteUser());
		ArrayList<TableList> tablelist = dao.getParentNameList(request.getRemoteUser(), true, "");
		ArrayList<SimpleComboBean> confList = new ArrayList<>();
		for (TableList tl : tablelist) {
			SimpleComboBean bean = new SimpleComboBean();
			bean.setId(tl.getTableID());
			bean.setName(tl.getName());
			confList.add(bean);
		}
		Logger.end(this, "getConfList");
		return confList;
	}

	public List<SimpleComboBean> getRolesList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getRolesList");
		/*
		 * DMWAdminDao dao = new DMWAdminDao(); ArrayList<SimpleComboBean>
		 * combolist = dao.getRolesList(request.getRemoteUser());
		 */

		List<SimpleComboBean> combolist = springDao.getRolesList(request.getRemoteUser());
		Logger.end(this, "getRolesList");
		return combolist;
	}

	public List<SimpleComboBean> getTableOwners(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableOwners");
		// DMWAdminDao dao = new DMWAdminDao();
		// ArrayList<SimpleComboBean> combolist =
		// dao.getTableOwners(request.getRemoteUser());
		List<SimpleComboBean> combolist = springDao.getTableOwners(request.getRemoteUser());
		Logger.end(this, "getTableOwners");
		return combolist;
	}

	public List<SimpleComboBean> getTablesList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableList");
		// DMWAdminDao dao = new DMWAdminDao();
		String owner = request.getParameter("owner");
		String tableName = request.getParameter("tableName");
		List<SimpleComboBean> combolist = springDao.getTablesList(owner, tableName, request.getRemoteUser());
		// List<SimpleComboBean> combolist = dao.getTablesList(owner, tableName,
		// request.getRemoteUser());
		Logger.end(this, "getTableList");
		return combolist;
	}

	public boolean saveNewTableInfo(HttpServletRequest request) throws DMWException {
		Logger.start(this, "saveNewTableInfo");
		// DMWAdminDao dao = new DMWAdminDao();
		boolean bool = false;
		String confId = request.getParameter("confId");
		String owner = request.getParameter("owner");
		String tableAlias = request.getParameter("alias");
		String tableName = request.getParameter("tableName");
		String schemaName = request.getParameter("schemaName");
		String systemDate = request.getParameter("systemDate");
		String systemUser = request.getParameter("systemUser");

		String roles = request.getParameter("roleName");
		String pivot = request.getParameter("pivot");
		// pagination logic
		String fixedCol = request.getParameter("fixedCol");
		String pivotCol = request.getParameter("pivotCol");
		String pivotData = request.getParameter("pivotData");
		String pagination = request.getParameter("pagination");
		// pagination logic
		String pageCount = request.getParameter("pageCount");
		String sortBy = request.getParameter("sortBy");
		String sortDir = request.getParameter("sortDir");

		String jsonLine = request.getParameter("additionalRole");
		String condQuery = request.getParameter("condQuery");
		String query = request.getParameter("query");
		TableInfo info = new TableInfo();
		info.setId(Integer.parseInt(confId.replaceAll("SIMPLE_COMBO-", "").trim()));
		info.setOwner(owner);
		info.setTableAlias(tableAlias);
		info.setTableName(tableName);
		info.setSchemaName(schemaName);
		info.setRoles(roles);
		if (pivot.equalsIgnoreCase("true")) {
			info.setPivot(true);
			info.setFixedFields(fixedCol);
			info.setPivotColumns(pivotCol);
			info.setPivotData(pivotData);
		}
		if (pagination.equalsIgnoreCase("true")) {
			info.setPaging(true);
			info.setPageLength(Integer.parseInt(pageCount));
			info.setSortBy((sortBy));
			info.setSortDir((sortDir));
		}

		/*
		 * if (editable.equalsIgnoreCase("true")) info.setEditable(true);
		 */
		if (condQuery.equalsIgnoreCase("true")) {
			info.setCondQuery(true);
			info.setQuery(query);
		}
		info.setSystemDate((systemDate));
		info.setSystemUser(systemUser);
		System.out.println(jsonLine);
		ArrayList<TableAccess> tableAccessList = new ArrayList<>();
		if (jsonLine != null) {
			JsonParser json = new JsonParser();
			JsonArray jArr = (JsonArray) json.parse(jsonLine);
			for (int i = 0; i < jArr.size(); i++) {
				TableAccess access = new TableAccess();
				JsonObject jObj = new JsonObject();
				jObj = (JsonObject) jArr.get(i);
				access.setAddRow(Boolean.parseBoolean(jObj.get("vAddRow").toString()));
				access.setDelRow(Boolean.parseBoolean(jObj.get("vDelRow").toString()));
				access.setDownload(Boolean.parseBoolean(jObj.get("vDownload").toString()));
				access.setEditable(Boolean.parseBoolean(jObj.get("vEditable").toString()));
				access.setMassUpdate(Boolean.parseBoolean(jObj.get("vMassUpdate").toString()));
				access.setUpload(Boolean.parseBoolean(jObj.get("vUpload").toString()));
				access.setRoleId(Integer.parseInt(jObj.get("vroleId").toString()));
				ArrayList<String> editableColumns = new ArrayList<>();
				if (!access.isEditable()) {
					String[] columnsArry = jObj.get("vEditableColumns").toString().split(",");
					for (String str : columnsArry)
						editableColumns.add(str);
				}
				access.setEditableColumns(editableColumns);
				tableAccessList.add(access);
				// System.out.println(jObj.get("vroleName"));
				// System.out.println(jObj.get("vEditableColumns"));
			}
		}
		info.setRole(false);

		/*
		 * if (roles != null && !roles.equalsIgnoreCase("")) { String[] role =
		 * roles.split(","); for (int i = 0; i < role.length; i++) { if
		 * (role[i].trim().equalsIgnoreCase("-1")) { info.setRole(true); break;
		 * } } }
		 */

		bool = springDao.saveNewTableInfo(info, tableAccessList, request.getRemoteUser());
		Logger.end(this, "saveNewTableInfo");
		return bool;
	}

	public List<SimpleComboBean> getColumnsList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getColumnsList");
		// DMWAdminDao dao = new DMWAdminDao();
		String owner = request.getParameter("owner");
		String tableName = request.getParameter("tableName");
		List<SimpleComboBean> combolist = springDao.getColumnsList(owner, tableName, request.getRemoteUser());
		Logger.end(this, "getColumnsList");
		return combolist;
	}

}
