package com.mxim.dmw.datamanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mxim.dmw.dao.DMWAuditDao;
import com.mxim.dmw.dao.DMWDao;
import com.mxim.dmw.dao.DMWMessageQueue;
import com.mxim.dmw.domain.DynamicBean;
import com.mxim.dmw.domain.SimpleComboBean;
import com.mxim.dmw.domain.TableInfo;
import com.mxim.dmw.domain.TableList;
import com.mxim.dmw.domain.TableUsers;
import com.mxim.dmw.domain.UserInfo;
import com.mxim.dmw.domain.ViewException;
import com.mxim.dmw.exception.DMWException;
import com.mxim.dmw.util.AuditMessage;
import com.mxim.dmw.util.FormatUtil;
import com.mxim.dmw.util.Logger;

public class DMWManager {
	// DMWAuditDao audit = new DMWAuditDao();
	DMWMessageQueue audit = new DMWMessageQueue();

	public DMWManager() {
		// TODO Auto-generated constructor stub
	}

	public UserInfo getUser(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getUserDetails");
		UserInfo user = null;
		if (request.getSession().getAttribute("USER_DETAILS") == null) {
			DMWDao dao = new DMWDao();
			// System.out.println(request.getRemoteUser());
			user = dao.getUserDetails(request.getRemoteUser());
			request.getSession().setAttribute("USER_DETAILS", user);
			audit.insertAuditLog("getUser", "get new user details", "", request.getRemoteUser());
		} else {
			user = (UserInfo) request.getSession().getAttribute("USER_DETAILS");
			setApplicationUsers(request, -1);
			audit.insertAuditLog("getUser", "get duplicate session details", "", request.getRemoteUser());
		}
		Logger.end(this, "getUserDetails");
		return user;
	}

	public DynamicBean getTableInfo(String tableId, HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableInfo");
		DMWDao dao = new DMWDao();
		DynamicBean dBean;
		UserInfo user = (UserInfo) request.getSession().getAttribute("USER_DETAILS");
		TableInfo tableInfo = dao.getTableInformation(tableId, user.getRoles(), request.getRemoteUser());

		if (user.isAdmin()) {
			tableInfo.setAddRow(true);
			tableInfo.setDeleteRow(true);
			tableInfo.setUpload(true);
			tableInfo.setDownload(true);
			tableInfo.setEditable(true);
			audit.insertAuditLog("getTableInfo", "Admin Role", "", request.getRemoteUser());
		}
		if (request.getSession().getAttribute("CURRENT_USERS") != null) {
			request.getSession().removeAttribute("CURRENT_USERS");
		}
		request.getSession().setAttribute("TableInfo", tableInfo);

		setApplicationUsers(request, tableInfo.getId());
		if (request.getParameter("pivot") != null && request.getParameter("pivot").equalsIgnoreCase("true")) {
			tableInfo.setPivot(true);
		}
		dBean = dao.getTableInfo(null, tableInfo, user.getUserId());
		dBean.setTableInfo(tableInfo);
		request.getSession().setAttribute("TABLE_COLUMN_TYPES", dBean.getHeader().getColumnTypes());
		request.getSession().setAttribute("TABLE_COLUMN_NAMES", dBean.getHeader().getColumnNames());
		HashMap<String, String> hm = getSystemValues(tableInfo, user.getUserId());
		request.getSession().setAttribute("TABLE_SYSTEM_VALUES", hm);
		if (tableInfo != null && !tableInfo.isEditable()) {
			dBean = dao.getEditableColumns(dBean, tableInfo);
		} else {
			if (tableInfo.isPivot()) {

			}
		}
		Logger.end(this, "getTableInfo");
		return dBean;
	}

	@SuppressWarnings("unchecked")
	public synchronized void setApplicationUsers_bkp(HttpServletRequest request, String change) {
		ServletContext application = request.getServletContext();
		TableInfo info = (TableInfo) request.getSession().getAttribute("TableInfo");
		if (change.equalsIgnoreCase("add")) {
			if (application.getAttribute("TABLE_USERS") == null) {
				HashMap<String, Integer> usersMap = new HashMap<>();
				usersMap.put(request.getRemoteUser(), info.getId());
				application.setAttribute("TABLE_USERS", usersMap);
				// //System.out.println("*************** " + usersMap.size());
			} else {
				HashMap<String, Integer> usersMap = (HashMap<String, Integer>) application.getAttribute("TABLE_USERS");
				usersMap.put(request.getRemoteUser(), info.getId());
				application.setAttribute("TABLE_USERS", usersMap);
				// System.out.println("*************** " + usersMap.size());
			}
		} else {
			HashMap<String, Integer> usersMap = (HashMap<String, Integer>) application.getAttribute("TABLE_USERS");
			if (usersMap != null)
				usersMap.remove(request.getRemoteUser());
			application.setAttribute("TABLE_USERS", usersMap);
			// System.out.println("*************** " + usersMap.size());
		}
		if (application.getAttribute("TABLE_USERS") != null) {
			HashMap<String, Integer> usersMap = (HashMap<String, Integer>) application.getAttribute("TABLE_USERS");
			Map<Integer, Integer> result = new TreeMap<Integer, Integer>();
			for (Map.Entry<String, Integer> entry : usersMap.entrySet()) {
				int value = entry.getValue();
				Integer count = result.get(value);
				if (count == null)
					result.put(value, new Integer(1));
				else
					result.put(value, new Integer(count + 1));
			}
			// System.out.println("Final values : " + result.values());
		}
	}

	public void setApplicationUsers(HttpServletRequest request, int tableId) {
		ServletContext application = request.getServletContext();
		application.setAttribute(request.getRemoteUser(), tableId);
	}

	@SuppressWarnings("unchecked")
	public DynamicBean getTableDetails(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableDetails");
		DMWDao dao = new DMWDao();
		DynamicBean dBean = null;
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_NAMES");
		LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_TYPES");
		int startRec = 0;
		int endRec = tableInfo.getEndRec();
		if (request.getParameter("start") != null) {
			startRec = Integer.parseInt(request.getParameter("start"));
		}
		if (request.getParameter("limit") != null) {
			endRec = startRec + Integer.parseInt(request.getParameter("limit"));
		}
		String sort = request.getParameter("sort");
		String dir = request.getParameter("dir");
		// parsing json Object
		String jsonLine = request.getParameter("filter");

		if (tableInfo != null) {
			tableInfo.setStartRec(startRec);
			tableInfo.setEndRec(endRec);
			if (sort != null)
				tableInfo.setSortBy(sort);
			if (dir != null)
				tableInfo.setSortDir(dir);
			if (request.getParameter("pivot") != null && request.getParameter("pivot").equalsIgnoreCase("true")) {
				tableInfo.setPivot(true);
			}
			if (request.getParameter("upload") != null && request.getParameter("upload").equalsIgnoreCase("true"))
				dBean = (DynamicBean) request.getSession().getAttribute("UPLOAD_DATA");
			else {
				dBean = dao.getTableDetails(jsonLine, tableInfo, columnNames, columnTypes);
				// System.out.println("*************** " +
				// dBean.getTotalCount());
			}
		}
		// used to save filters
		if (jsonLine == null) {
			request.getSession().setAttribute("TABLE_FILTERS", null);
		} else {
			request.getSession().setAttribute("TABLE_FILTERS", jsonLine);
		}
		Logger.end(this, "getTableDetails");
		return dBean;
	}

	ArrayList<TableList> gblParentList = new ArrayList<>();
	ArrayList<TableList> tableNameslist = new ArrayList<>();

	public ArrayList<TableList> getParentNameList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getParentNameList");
		if (request.getParameter("node").toString().equalsIgnoreCase("root")) {
			DMWDao dao = new DMWDao();
			UserInfo user = null;
			if (request.getSession().getAttribute("USER_DETAILS") != null) {
				user = (UserInfo) request.getSession().getAttribute("USER_DETAILS");
			} else {
				user = getUser(request);
			}
			String roles = "-1";
			for (String role : user.getRoles()) {
				roles += "," + role;
			}
			ArrayList<TableList> tablelist = dao.getParentNameList(request.getRemoteUser(), false, roles);
			tableNameslist = dao.getTableNameList(request.getRemoteUser(), roles);
			for (TableList tl : tablelist) {
				if (tl.getParentId() == 0) {
					gblParentList.add(tl);
				}
			}
			tablelist.removeAll(gblParentList);
			/*
			 * ArrayList<TableList> childlist = new ArrayList<>(); for (int k =
			 * 0; k < gblParentList.size(); k++) { for (int m = 0; m <
			 * tableNameslist.size(); m++) { if
			 * (tableNameslist.get(m).getParentId() ==
			 * gblParentList.get(k).getTableID()) {
			 * //tableNameslist.get(m).setParentId(gblParentList.get(k).
			 * getParentId( )); childlist.add(tableNameslist.get(m));
			 * tableNameslist.remove(m); break; } }
			 * gblParentList.get(k).setChildren(childlist); }
			 */
			if (tablelist.size() > 0) {
				processTreeList(gblParentList, tablelist);
				processTreeList(gblParentList, tableNameslist);
			} else {
				for (int k = 0; k < gblParentList.size(); k++) {
					ArrayList<TableList> childlist = new ArrayList<>();
					for (int m = 0; m < tableNameslist.size(); m++) {
						if (tableNameslist.get(m).getParentId() == gblParentList.get(k).getTableID()) {
							// tableNameslist.get(m).setParentId(gblParentList.get(k).getParentId());
							childlist.add(tableNameslist.get(m));
							// tableNameslist.remove(m);
							// break;
						}
					}
					// System.out.println(childlist.size());
					gblParentList.get(k).setChildren(childlist);
				}
			}
			Logger.end(this, "getParentNameList");
			/*
			 * tableNameslist = new ArrayList<>(); for (TableList tl :
			 * gblParentList) { // System.out.println(tl.getName()); if
			 * (tl.getChildren().size() != 0) tableNameslist.add(tl); } return
			 * tableNameslist;
			 */
			return gblParentList;
		}
		return null;
	}

	private ArrayList<TableList> processTreeList(ArrayList<TableList> parentlist, ArrayList<TableList> tableList) {
		ArrayList<TableList> myList = new ArrayList<>();
		ArrayList<TableList> childlist = new ArrayList<>();
		if (tableList.size() > 0) {

			for (int k = 0; k < parentlist.size(); k++) {
				if (parentlist.get(k).getChildren() == null)
					childlist = new ArrayList<>();
				else
					childlist = parentlist.get(k).getChildren();
				for (int i = 0; i < tableList.size(); i++) {
					if (parentlist.get(k).getTableID() == tableList.get(i).getParentId()) {

						childlist.add(tableList.get(i));

						/*System.out.println("** " + tableList.get(i).getParentId() + "|" + tableList.get(i).getTableID()
								+ "|" + tableList.get(i).getText());*/
					}
					/*
					 * for (int m = 0; m < tableNameslist.size(); m++) { if
					 * (tableNameslist.get(m).getParentId() ==
					 * parentlist.get(k).getTableID()) { //
					 * tableNameslist.get(m).setParentId(parentlist.get(k).
					 * getTableID());
					 * System.out.println(tableNameslist.get(m).getParentId() +
					 * "|" + tableNameslist.get(m).getTableID() + "|" +
					 * tableNameslist.get(m).getText());
					 * childlist.add(tableNameslist.get(m));
					 * tableNameslist.remove(m); // break; } }
					 */
				}
				parentlist.get(k).setChildren(childlist);
				gblParentList.addAll(processTreeList(childlist, tableList));
			}
		}
		return myList;
	}

	public String downloadCsv(DynamicBean dBean, HttpServletRequest request) {

		Logger.start(this, "downloadCsv");
		Logger.debug(this, "downloadCsv", "Started the download...", null, null);
		String fileName = null;
		Calendar cal = Calendar.getInstance();
		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < dBean.getData().size(); i++) {
				if (i == 0) {
					for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							sb.append(entry.getKey());
							sb.append(",");
						}
					}
					sb.append("\n");
				}
				for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
					if (!entry.getKey().equalsIgnoreCase("RID")) {
						sb.append("\"" + entry.getValue() + "\"");
						sb.append(",");
					}
				}
				sb.append("\n");
			}
			TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
			fileName = "DMW" + "_" + tableInfo.getTableName() + "_" + cal.getTimeInMillis() + ".csv";

			File file = null;

			/*
			 * if (System.getProperty("os.name").indexOf("Windows") > -1) { file
			 * = new File("c://DMW//" + fileName); } else // If Unix { file =
			 * new File("/var/lib/tomcat/webapps/web-commons/files/" +
			 * fileName); }
			 */
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");
			fileName = appPath + "/" + fileName;
			file = new File(fileName);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(sb.toString());
			fileWriter.flush();
			fileWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(this, "downloadCsv", "Exception :", null, e);
		} finally

		{

		}
		Logger.debug(this, "downloadCsv", "Ended the download...", null, null);
		Logger.end(this, "downloadCsv");
		return fileName;

	}

	public boolean insertTableDetails(HttpServletRequest request) throws DMWException {
		Logger.start(this, "insertTableDetails");
		boolean bool = false;
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		Logger.info(this, "insertTableDetails", "got tableInfo from session...", null, null);
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_TYPES");
		Logger.info(this, "insertTableDetails", "got columnTypes from session...", null, null);
		audit.insertMessageLog("insertTableDetails", "frameUpdateQuery", "", request.getRemoteUser(), "", "");
		ArrayList<String> queryList = null;
		@SuppressWarnings("unchecked")
		HashMap<String, String> hm = (HashMap<String, String>) request.getSession().getAttribute("TABLE_SYSTEM_VALUES");
		if (tableInfo != null && tableInfo.isPivot()) {
			queryList = framePivotInsertQuery(request, tableInfo, columnTypes);
		} else {
			queryList = frameInsertQuery(request, tableInfo, columnTypes, hm);
		}
		DMWDao dao = new DMWDao();
		// bool = dao.executeTableSave(queryList, request.getRemoteUser());
		bool = dao.executeTableSave(tableInfo, queryList, request.getRemoteUser());
		Logger.debug(this, "insertTableDetails", "Ended of Query framing...", null, null);
		Logger.end(this, "insertTableDetails");
		return bool;
	}

	public boolean updateTableDetails(HttpServletRequest request) throws DMWException {
		Logger.start(this, "updateTableDetails");
		boolean bool = false;
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		Logger.info(this, "updateTableDetails", "got tableInfo from session...", null, null);
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_TYPES");
		Logger.info(this, "updateTableDetails", "got columnTypes from session...", null, null);
		ArrayList<String> queryList = null;
		@SuppressWarnings("unchecked")
		HashMap<String, String> hm = (HashMap<String, String>) request.getSession().getAttribute("TABLE_SYSTEM_VALUES");
		if (tableInfo != null && tableInfo.isPivot()) {
			queryList = framePivotUpdateQuery(request, tableInfo, columnTypes);
		} else {
			queryList = frameUpdateQuery(request, tableInfo, columnTypes, hm);
		}
		DMWDao dao = new DMWDao();
		// bool = dao.executeTableSave(queryList, request.getRemoteUser());
		bool = dao.executeTableSave(tableInfo, queryList, request.getRemoteUser());
		Logger.debug(this, "updateTableDetails", "Ended of Query framing...", null, null);
		Logger.end(this, "updateTableDetails");
		return bool;
	}

	@SuppressWarnings("unchecked")
	public boolean deleteTableData(HttpServletRequest request) throws DMWException {
		Logger.start(this, "deleteTableData");
		boolean bool = false;
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		Logger.info(this, "deleteTableData", "got tableInfo from session...", null, null);
		ArrayList<String> queryList = new ArrayList<>();
		audit.insertMessageLog("deleteTableData", "Deleting the records", "", request.getRemoteUser(), "", "");
		if (tableInfo != null && tableInfo.isPivot()) {
			LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_TYPES");
			queryList = framePivotDeleteQuery(request, tableInfo, columnTypes);
		} else {
			queryList = frameDeleteQuery(request, tableInfo);
		}
		DMWDao dao = new DMWDao();
		/*
		 * LinkedHashMap<String, String> columnNames = (LinkedHashMap<String,
		 * String>) request.getSession() .getAttribute("TABLE_COLUMN_NAMES");
		 */
		bool = dao.executeTableSave(tableInfo, queryList, request.getRemoteUser());
		Logger.debug(this, "deleteTableData", "Ended of Query framing...", null, null);
		Logger.end(this, "deleteTableData");
		return bool;
	}

	public ArrayList<String> frameInsertQuery(HttpServletRequest request, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes, HashMap<String, String> hm) {
		ArrayList<String> queryList = new ArrayList<>();
		try {

			String body = request.getReader().readLine();
			Logger.info(this, "frameInsertQuery", "reading the body...", null, null);
			JsonParser parser = new JsonParser();
			JsonElement tradeElement = parser.parse(body);
			if (tradeElement.isJsonArray()) {
				JsonArray jArray = tradeElement.getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {
					StringBuffer query = new StringBuffer(
							"INSERT INTO " + tableInfo.getOwner() + "." + tableInfo.getTableName() + "(");
					JsonObject json = (JsonObject) parser.parse(jArray.get(i).toString());
					Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
					for (Map.Entry<String, JsonElement> entry : entries) {
						// System.out.println(entry.getKey());
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							query.append(entry.getKey() + ",");
						}
					}
					query.deleteCharAt(query.length() - 1);
					query.append(") VALUES (");
					for (Map.Entry<String, JsonElement> entry : entries) {
						// System.out.println(entry.getKey());
						if (!entry.getKey().equalsIgnoreCase("RID")) {

							if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
								if (hm.get("SYSTEM_DATE") != null
										&& hm.get("SYSTEM_DATE").equalsIgnoreCase(entry.getKey())) {
									query.append("SYSDATE,");
								} else {
									String date = FormatUtil
											.formatDate(entry.getValue().toString().replaceAll("\"", ""));
									query.append("to_date('" + date + "','mm/dd/yyyy'),");
								}
							} else if (columnTypes.get(entry.getKey()).equalsIgnoreCase("number")) {
								if (entry.getValue().toString() != null
										&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("null")
										&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("")) {
									query.append(
											"'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
													+ "',");
								} else {
									query.append("null,");
								}
							} else {
								if (hm.get("SYSTEM_USER") != null
										&& hm.get("SYSTEM_USER").equalsIgnoreCase(entry.getKey())) {
									query.append("'" + request.getRemoteUser() + "',");
								} else {
									if (entry.getValue().toString() != null
											&& !entry.getValue().toString().replaceAll("\"", "")
													.equalsIgnoreCase("null")
											&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("")) {
										query.append("'"
												+ entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
												+ "',");
									} else {
										query.append("null,");
									}
								}
							}
						}
					}
					query.deleteCharAt(query.length() - 1);
					query.append(")");
					System.out.println(query);
					Logger.info(this, "frameInsertQuery", "Insert Query..." + query, null, null);
					queryList.add(query.toString());
				}

			} else {
				StringBuffer query = new StringBuffer(
						"INSERT INTO " + tableInfo.getOwner() + "." + tableInfo.getTableName() + "(");
				JsonObject json = (JsonObject) parser.parse(body);
				Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
				for (Map.Entry<String, JsonElement> entry : entries) {
					// System.out.println(entry.getKey());
					if (!entry.getKey().equalsIgnoreCase("RID")) {
						query.append(entry.getKey() + ",");
					}
				}
				query.deleteCharAt(query.length() - 1);
				query.append(") VALUES (");
				for (Map.Entry<String, JsonElement> entry : entries) {
					// System.out.println(entry.getKey());
					if (!entry.getKey().equalsIgnoreCase("RID")) {

						if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
							if (hm.get("SYSTEM_DATE") != null
									&& hm.get("SYSTEM_DATE").equalsIgnoreCase(entry.getKey())) {
								query.append("SYSDATE,");
							} else {
								String date = FormatUtil.formatDate(entry.getValue().toString().replaceAll("\"", ""));
								query.append("to_date('" + date + "','mm/dd/yyyy'),");
							}
						} else if (columnTypes.get(entry.getKey()).equalsIgnoreCase("number")) {
							if (entry.getValue().toString() != null
									&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("null")
									&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("")) {
								query.append(
										"'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
												+ "',");
							} else {
								query.append("null,");
							}
						} else {
							if (hm.get("SYSTEM_USER") != null
									&& hm.get("SYSTEM_USER").equalsIgnoreCase(entry.getKey())) {
								query.append("'" + request.getRemoteUser() + "',");
							} else {
								if (entry.getValue().toString() != null
										&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("null")
										&& !entry.getValue().toString().replaceAll("\"", "").equalsIgnoreCase("")) {
									query.append(
											"'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
													+ "',");
								} else {
									query.append("null,");
								}
							}
						}
					}
				}
				query.deleteCharAt(query.length() - 1);
				query.append(")");
				Logger.info(this, "frameInsertQuery", "Insert Query..." + query, null, null);
				System.out.println(query);
				queryList.add(query.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	public ArrayList<String> framePivotInsertQuery(HttpServletRequest request, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes) {
		ArrayList<String> queryList = new ArrayList<>();
		try {

			String body = request.getReader().readLine();
			Logger.info(this, "framePivotInsertQuery", "reading the body...", null, null);
			JsonParser parser = new JsonParser();
			JsonElement tradeElement = parser.parse(body);
			if (tradeElement.isJsonArray()) {
				JsonArray jArray = tradeElement.getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {
					StringBuffer query = new StringBuffer("INSERT INTO " + tableInfo.getOwner() + "."
							+ tableInfo.getTableName() + "(" + tableInfo.getFixedFields() + ","
							+ tableInfo.getPivotColumns() + "," + tableInfo.getPivotData());
					JsonObject json = (JsonObject) parser.parse(jArray.get(i).toString());
					Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
					query.append(") VALUES (");
					int k = 0;
					String[] strArr = tableInfo.getFixedFields().split(",");
					for (Map.Entry<String, JsonElement> entry : entries) {
						// System.out.println(entry.getKey());
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							for (String str : strArr) {
								if (entry.getKey().equalsIgnoreCase(str)) {
									if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
										String date = FormatUtil
												.formatDate(entry.getValue().toString().replaceAll("\"", ""));
										query.append("to_date('" + date + "','mm/dd/yyyy'),");
									} else {
										query.append("'"
												+ entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
												+ "',");
									}
									break;
								}
							}

						}
					}
					for (Map.Entry<String, JsonElement> entry : entries) {
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							k++;
							// System.out.println(entry.getKey() + "|" + k);
							if (k > strArr.length) {// condition to ignore first
													// Pivot columns
								StringBuffer query_ = new StringBuffer(query);

								query_.append("'" + entry.getKey().toString().replaceAll("'", "''").replaceAll("\"", "")
										+ "',");
								if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
									String date = FormatUtil
											.formatDate(entry.getValue().toString().replaceAll("\"", ""));
									query_.append("to_date('" + date + "','mm/dd/yyyy'),");
								} else {
									query_.append(
											"'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
													+ "',");
								}
								query_.deleteCharAt(query_.length() - 1);
								query_.append(")");
								queryList.add(query_.toString());
								Logger.info(this, "framePivotInsertQuery", "Insert Query..." + query_, null, null);
							}
						}
					}
				}

			} else {
				StringBuffer query = new StringBuffer("INSERT INTO " + tableInfo.getOwner() + "."
						+ tableInfo.getTableName() + "(" + tableInfo.getFixedFields() + ","
						+ tableInfo.getPivotColumns() + "," + tableInfo.getPivotData());
				JsonObject json = (JsonObject) parser.parse(body);
				Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
				query.append(") VALUES (");
				int k = 0;
				String[] strArr = tableInfo.getFixedFields().split(",");
				for (Map.Entry<String, JsonElement> entry : entries) {
					// System.out.println(entry.getKey());
					if (!entry.getKey().equalsIgnoreCase("RID")) {
						for (String str : strArr) {
							if (entry.getKey().equalsIgnoreCase(str)) {
								if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
									String date = FormatUtil
											.formatDate(entry.getValue().toString().replaceAll("\"", ""));
									query.append("to_date('" + date + "','mm/dd/yyyy'),");
								} else {
									query.append(
											"'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
													+ "',");
								}
								break;
							}
						}

					}
				}
				for (Map.Entry<String, JsonElement> entry : entries) {
					if (!entry.getKey().equalsIgnoreCase("RID")) {
						k++;
						// System.out.println(entry.getKey() + "|" + k);
						if (k > strArr.length) {// condition to ignore first
												// Pivot columns
							StringBuffer query_ = new StringBuffer(query);

							query_.append(
									"'" + entry.getKey().toString().replaceAll("'", "''").replaceAll("\"", "") + "',");
							if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
								String date = FormatUtil.formatDate(entry.getValue().toString().replaceAll("\"", ""));
								query_.append("to_date('" + date + "','mm/dd/yyyy'),");
							} else {
								query_.append(
										"'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
												+ "',");
							}
							query_.deleteCharAt(query_.length() - 1);
							query_.append(")");
							queryList.add(query_.toString());
							Logger.info(this, "framePivotInsertQuery", "Insert Query..." + query_, null, null);
						}
					}
				}
			}
		} catch (

		IOException e)

		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;

	}

	public ArrayList<String> framePivotUpdateQuery(HttpServletRequest request, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes) {
		ArrayList<String> queryList = new ArrayList<>();
		try {

			String body = request.getReader().readLine();
			Logger.info(this, "framePivotUpdateQuery", "reading the body...", null, null);
			JsonParser parser = new JsonParser();
			JsonElement tradeElement = parser.parse(body);
			if (tradeElement.isJsonArray()) {
				JsonArray jArray = tradeElement.getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {
					StringBuffer query = new StringBuffer("UPDATE " + tableInfo.getOwner() + "."
							+ tableInfo.getTableName() + "   SET " + tableInfo.getPivotData() + "=':PIVOT_DATA'");
					JsonObject json = (JsonObject) parser.parse(jArray.get(i).toString());
					Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
					query.append(" WHERE " + tableInfo.getPivotColumns() + "=':PIVOT_COLUMNS'");
					query.append(" AND " + tableInfo.getFixedFields().replaceAll(",", "||") + "=':FIXED_COUMNS'");
					String actualQuery = query.toString();
					for (Map.Entry<String, JsonElement> entry : entries) {
						if (entry.getKey().equalsIgnoreCase("RID")) {
							actualQuery = actualQuery.replaceAll(":FIXED_COUMNS", entry.getValue().toString()
									.replaceAll("'", "''").replaceAll("\"", "").replaceAll("\\|", "'\\|\\|'"));
							// actualQuery = actualQuery.replaceAll("|",
							// "'||'");
						} else {
							actualQuery = actualQuery.replaceAll(":PIVOT_COLUMNS",
									entry.getKey().toString().replaceAll("'", "''").replaceAll("\"", ""));
							actualQuery = actualQuery.replaceAll(":PIVOT_DATA",
									entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", ""));
						}

					}
					queryList.add(actualQuery);
					Logger.info(this, "framePivotUpdateQuery", "Update Query..." + actualQuery, null, null);
				}

			} else {
				StringBuffer query = new StringBuffer("UPDATE " + tableInfo.getOwner() + "." + tableInfo.getTableName()
						+ "   SET " + tableInfo.getPivotData() + "=':PIVOT_DATA'");
				JsonObject json = (JsonObject) parser.parse(body);
				Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
				query.append(" WHERE " + tableInfo.getPivotColumns() + "=':PIVOT_COLUMNS'");
				query.append(" AND " + tableInfo.getFixedFields().replaceAll(",", "||") + "=':FIXED_COUMNS'");
				String actualQuery = query.toString();
				for (Map.Entry<String, JsonElement> entry : entries) {
					if (entry.getKey().equalsIgnoreCase("RID")) {
						actualQuery = actualQuery.replaceAll(":FIXED_COUMNS",
								entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
										.replaceAll("\"", "").replaceAll("\\|", "'\\|\\|'"));
						// actualQuery = actualQuery.replaceAll("|", "'||'");
					} else {
						actualQuery = actualQuery.replaceAll(":PIVOT_COLUMNS",
								entry.getKey().toString().replaceAll("'", "''").replaceAll("\"", ""));
						actualQuery = actualQuery.replaceAll(":PIVOT_DATA",
								entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", ""));
					}

				}
				queryList.add(actualQuery);
				Logger.info(this, "framePivotUpdateQuery", "Update Query..." + actualQuery, null, null);
			}
			// System.out.println(queryList.toString());
		} catch (

		IOException e)

		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;

	}

	public ArrayList<String> framePivotDeleteQuery(HttpServletRequest request, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes) {
		Logger.start(this, "framePivotDeleteQuery");
		ArrayList<String> queryList = new ArrayList<>();
		try {

			String body = request.getReader().readLine();
			Logger.info(this, "framePivotDeleteQuery", "reading the body...", null, null);
			JsonParser parser = new JsonParser();
			JsonElement tradeElement = parser.parse(body);
			if (tradeElement.isJsonArray()) {
				JsonArray jArray = tradeElement.getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {

					StringBuffer query = new StringBuffer(
							"DELETE FROM " + tableInfo.getOwner() + "." + tableInfo.getTableName());
					JsonObject json = (JsonObject) parser.parse(jArray.get(i).toString());
					Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
					query.append(" WHERE " + tableInfo.getFixedFields().replaceAll(",", "||") + "=':FIXED_COUMNS'");
					String actualQuery = query.toString();
					for (Map.Entry<String, JsonElement> entry : entries) {
						if (entry.getKey().equalsIgnoreCase("RID")) {
							actualQuery = actualQuery.replaceAll(":FIXED_COUMNS", entry.getValue().toString()
									.replaceAll("'", "''").replaceAll("\"", "").replaceAll("\\|", "'\\|\\|'"));
						}
					}
					queryList.add(actualQuery);
					Logger.info(this, "framePivotDeleteQuery", "framePivotDeleteQuery Query..." + actualQuery, null,
							null);
				}

			} else {
				StringBuffer query = new StringBuffer(
						"DELETE FROM " + tableInfo.getOwner() + "." + tableInfo.getTableName());
				JsonObject json = (JsonObject) parser.parse(body);
				Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
				query.append(" WHERE " + tableInfo.getFixedFields().replaceAll(",", "||") + "=':FIXED_COUMNS'");
				String actualQuery = query.toString();
				for (Map.Entry<String, JsonElement> entry : entries) {
					if (entry.getKey().equalsIgnoreCase("RID")) {
						actualQuery = actualQuery.replaceAll(":FIXED_COUMNS", entry.getValue().toString()
								.replaceAll("'", "''").replaceAll("\"", "").replaceAll("\\|", "'\\|\\|'"));
					}
				}
				queryList.add(actualQuery);
				Logger.info(this, "framePivotDeleteQuery", "framePivotDeleteQuery Query..." + actualQuery, null, null);
			}
			// System.out.println(queryList.toString());
		} catch (

		IOException e)

		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.end(this, "framePivotDeleteQuery");
		return queryList;

	}

	public ArrayList<String> frameUpdateQuery(HttpServletRequest request, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes, HashMap<String, String> hm) throws DMWException {
		ArrayList<String> queryList = new ArrayList<>();
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_NAMES");
		String newValue = null;
		StringBuffer bkpQuery = new StringBuffer("select ");
		try {

			String body = request.getReader().readLine();
			Logger.info(this, "frameUpdateQuery", "reading the body...", null, null);
			JsonParser parser = new JsonParser();
			JsonElement tradeElement = parser.parse(body);
			String rowId = "";

			if (tradeElement.isJsonArray()) {
				JsonArray jArray = tradeElement.getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {
					bkpQuery = new StringBuffer("select ");
					StringBuffer query = new StringBuffer(
							"UPDATE  " + tableInfo.getOwner() + "." + tableInfo.getTableName() + " SET ");
					JsonObject json = (JsonObject) parser.parse(jArray.get(i).toString());
					Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
					for (Map.Entry<String, JsonElement> entry : entries) {
						// System.out.println(entry.getKey());
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {

								if (hm.get("SYSTEM_DATE") != null
										&& hm.get("SYSTEM_DATE").equalsIgnoreCase(entry.getKey())) {
									query.append(entry.getKey() + "=SYSDATE,");
								} else {
									String date = FormatUtil
											.formatDate(entry.getValue().toString().replaceAll("\"", ""));
									query.append(entry.getKey() + "=to_date('" + date + "','mm/dd/yyyy'),");
								}
							} else {
								if (hm.get("SYSTEM_USER") != null
										&& hm.get("SYSTEM_USER").equalsIgnoreCase(entry.getKey())) {
									query.append(entry.getKey() + "='" + request.getRemoteUser() + "',");
								} else {
									query.append(entry.getKey() + "='"
											+ entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
											+ "',");
								}

							}
							bkpQuery.append(entry.getKey() + " ,");
							newValue = entry.getValue().toString();
						} else {
							rowId = entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "");
						}
					}
					query.deleteCharAt(query.length() - 1);
					query.append(" WHERE " + columnNames.get("RID") + "='" + rowId + "'");
					Logger.info(this, "frameUpdateQuery", "Update Query..." + query, null, null);
					bkpQuery.deleteCharAt(bkpQuery.length() - 1);
					bkpQuery.append(" FROM   "+tableInfo.getOwner() + "." + tableInfo.getTableName());
					bkpQuery.append(" WHERE " + columnNames.get("RID") + "='" + rowId + "'");
					DMWDao dao = new DMWDao();
					System.out.println(bkpQuery.toString());
					ArrayList<String> al = dao.getOldValues(bkpQuery.toString(), request.getRemoteUser());
					for (String mess : al) {
						audit.insertMessageLog("updateTableDetails", query.toString(), "", request.getRemoteUser(),
								mess, newValue);
					}
					System.out.println(query.toString());
					queryList.add(query.toString());
				}

			} else {
				bkpQuery = new StringBuffer("select ");
				StringBuffer query = new StringBuffer(
						"UPDATE  " + tableInfo.getOwner() + "." + tableInfo.getTableName() + " SET ");
				JsonObject json = (JsonObject) parser.parse(body);
				Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
				
				for (Map.Entry<String, JsonElement> entry : entries) {
					// System.out.println(entry.getKey());
					if (!entry.getKey().equalsIgnoreCase("RID")) {
						if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {

							if (hm.get("SYSTEM_DATE") != null
									&& hm.get("SYSTEM_DATE").equalsIgnoreCase(entry.getKey())) {
								query.append(entry.getKey() + "=SYSDATE,");
							} else {
								String date = FormatUtil.formatDate(entry.getValue().toString().replaceAll("\"", ""));
								query.append(entry.getKey() + "=to_date('" + date + "','mm/dd/yyyy'),");
								
							}
						} else {
							if (hm.get("SYSTEM_USER") != null
									&& hm.get("SYSTEM_USER").equalsIgnoreCase(entry.getKey())) {
								query.append(entry.getKey() + "='" + request.getRemoteUser() + "',");
							} else {
								query.append(entry.getKey() + "='"
										+ entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
										+ "',");
							}

						}
						bkpQuery.append(entry.getKey() + " ,");
						newValue = entry.getValue().toString();
					} else {
						rowId = entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "");
					}
				}
				query.deleteCharAt(query.length() - 1);
				query.append(" WHERE " + columnNames.get("RID") + "='" + rowId + "'");
				bkpQuery.deleteCharAt(bkpQuery.length() - 1);
				bkpQuery.append(" FROM   "+tableInfo.getOwner() + "." + tableInfo.getTableName());
				bkpQuery.append(" WHERE " + columnNames.get("RID") + "='" + rowId + "'");
				Logger.info(this, "frameUpdateQuery", "Update Query..." + query, null, null);
				DMWDao dao = new DMWDao();
				ArrayList<String> al = dao.getOldValues(bkpQuery.toString(), request.getRemoteUser());
				for (String mess : al) {
					audit.insertMessageLog("updateTableDetails", query.toString(), "", request.getRemoteUser(), mess,
							newValue);
				}
				// audit.insertMessageLog("Update", "frameUpdateQuery",
				// query.toString(), request.getRemoteUser(), "", "");
				queryList.add(query.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	public ArrayList<String> frameDeleteQuery(HttpServletRequest request, TableInfo tableInfo) throws DMWException {
		ArrayList<String> queryList = new ArrayList<>();
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_NAMES");
		DMWDao dao = new DMWDao();
		try {
			String body = request.getReader().readLine();
			Logger.info(this, "frameDeleteQuery", "reading the body...", null, null);
			JsonParser parser = new JsonParser();
			JsonElement tradeElement = parser.parse(body);
			String rowId = "";
			if (tradeElement.isJsonArray()) {
				JsonArray jArray = tradeElement.getAsJsonArray();
				for (int i = 0; i < jArray.size(); i++) {

					StringBuffer query = new StringBuffer(
							"DELETE FROM  " + tableInfo.getOwner() + "." + tableInfo.getTableName() + " ");
					JsonObject json = (JsonObject) parser.parse(jArray.get(i).toString());
					Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
					for (Map.Entry<String, JsonElement> entry : entries) {
						// System.out.println(entry.getKey());
						if (entry.getKey().equalsIgnoreCase("RID")) {
							rowId = entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "");
							break;
						}
					}
					query.append(" WHERE " + columnNames.get("RID") + "='" + rowId + "'");
					Logger.info(this, "frameDeleteQuery", "Update Query..." + query, null, null);
					audit.insertMessageLog("frameDeleteQuery", "frameDeleteQuery", query.toString(),
							request.getRemoteUser(), "", "");
					queryList.add(query.toString());
					dao.backupDeleteTableData(tableInfo, columnNames,
							" WHERE " + columnNames.get("RID") + "='" + rowId + "'", request.getRemoteUser());
				}

			} else {
				StringBuffer query = new StringBuffer(
						"DELETE FROM  " + tableInfo.getOwner() + "." + tableInfo.getTableName() + " ");
				JsonObject json = (JsonObject) parser.parse(body);
				Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
				for (Map.Entry<String, JsonElement> entry : entries) {
					// System.out.println(entry.getKey());
					if (entry.getKey().equalsIgnoreCase("RID")) {
						rowId = entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "");
						break;
					}
				}
				query.append(" WHERE " + columnNames.get("RID") + "='" + rowId + "'");
				Logger.info(this, "frameDeleteQuery", "delete Query..." + query, null, null);
				audit.insertMessageLog("frameDeleteQuery", "frameDeleteQuery", query.toString(),
						request.getRemoteUser(), "", "");
				queryList.add(query.toString());
				dao.backupDeleteTableData(tableInfo, columnNames,
						" WHERE " + columnNames.get("RID") + "='" + rowId + "'", request.getRemoteUser());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> uploadCsvData(MultipartFile file, HttpServletRequest request) {

		String name = "Success";
		if (!file.isEmpty()) {
			// System.out.println( fileName.getName() );
			Logger.start(this, "uploadCsvData");
			long la = System.currentTimeMillis();
			String sessionId = request.getSession().getId();
			String filePath = "C://temp//";
			String fileName = file.getOriginalFilename();
			try {
				Logger.debug(this, "uploadCsvData", "filename : " + fileName + " ", null, null);

				if (System.getProperty("os.name").indexOf("Windows") > -1) {
					filePath = "C://DMW//Upload//";
				} else
				// If Unix
				{
					filePath = request.getSession().getServletContext().getRealPath("/");
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath += "/../apptemp/DMW/";
					System.out.println("***filepath****" + filePath);
				}
				File dir = new File(filePath);
				if (!dir.exists()) {
					if (dir.mkdir()) {
						System.out.println("Directory is created!");
					} else {
						System.out.println("Failed to create directory!");
					}
				}
				// reading the upload file.

				InputStream inputStream = (InputStream) file.getInputStream();
				FileOutputStream out_ = new FileOutputStream(filePath + file + sessionId + "_.csv", false);
				byte buf[] = new byte[1024];
				int len;
				// loading the content of upload file into out file
				while ((len = inputStream.read(buf)) > 0)
					out_.write(buf, 0, len);
				out_.close();
				BufferedReader br = new BufferedReader(new FileReader(filePath + file + sessionId + "_.csv"));
				String line = "";
				int k = 0;
				boolean bool = false;
				DynamicBean dbean = new DynamicBean();
				ArrayList<LinkedHashMap<String, String>> al = new ArrayList<>();
				String[] headerArr = null;
				LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
						.getAttribute("TABLE_COLUMN_NAMES");
				LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
						.getAttribute("TABLE_COLUMN_TYPES");
				HashMap<String, String> hm = (HashMap<String, String>) request.getSession()
						.getAttribute("TABLE_SYSTEM_VALUES");
				while ((line = br.readLine()) != null) {
					// use comma as separator
					String[] dataArr = line.split(",");
					if (k == 0) {
						headerArr = new String[dataArr.length];
						if (columnNames.size() == dataArr.length + 1) {
							for (int j = 0; j < dataArr.length; j++) {
								if (columnNames.get(dataArr[j].replaceAll("\"", "")) == null) {
									bool = true;
									return resultValue("Upload Failed", "The file is not matching with Grid.");
								}
								headerArr[j] = dataArr[j].replaceAll("\"", "");
								// System.out.println(data);
							}
						} else {
							// return "You failed to upload , because the file
							// is not matching with Grid.";
							return resultValue("Upload Failed", "The file is not matching with Grid.");
						}
					} else {
						if (bool) {
							// return "You failed to upload , because the file
							// is not matching with Grid.";
							return resultValue("Upload Failed", "The file is not matching with Grid.");
						} else {
							LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
							for (int j = 0; j < dataArr.length; j++) {

								try {
									if (columnTypes.get(headerArr[j]).equalsIgnoreCase("number"))
										lhm.put(headerArr[j], Double.parseDouble(dataArr[j].replaceAll("\"", "")) + "");
									else if (columnTypes.get(headerArr[j]).equalsIgnoreCase("date")) {
										System.out.println(dataArr[j].toString());
										if (hm.get("SYSTEM_DATE").equalsIgnoreCase(headerArr[j]))
											lhm.put(headerArr[j], dataArr[j].toString());
										else {
											SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											Date date = new Date();
											try {
												date = formatter.parse(dataArr[j].toString());
											} catch (ParseException e) {
												formatter = new SimpleDateFormat("yyyy-MM-dd");
												date = formatter.parse(dataArr[j].toString());
											}
											String pattern = "MM/dd/yyyy";
											SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
											// cell.setCellValue(simpleDateFormat.format(date));
											lhm.put(headerArr[j], simpleDateFormat.format(date));
										}
									} else
										lhm.put(headerArr[j], dataArr[j].replaceAll("\"", ""));

								} catch (NumberFormatException e) {
									return resultValue("Upload Failed",
											"Please correct the data in file and then retry.<br>   <b>  Hint : Header '"
													+ headerArr[j] + "' @ Row :" + (k + 1) + " should be number</b>");

									// TODO: handle exception
								} catch (NullPointerException e) {
									return resultValue("Upload Failed",
											"Please correct the data in file and then retry.<br>   <b>  Hint : Header '"
													+ headerArr[j] + "' @ Row :" + (k + 1) + " cannot be null</b>");

								}
								// System.out.println(row.getCell((short)
								// j).toString());

								// System.out.println(dataArr[j]);
							}
							al.add(lhm);

						}
					}
					k++;
				}
				br.close();
				dbean.setData(al);
				// System.out.println(al.size());
				request.getSession().setAttribute("UPLOAD_DATA", dbean);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(this, "uploadCsvData", "Exception :", null, e);
				File f = new File(filePath + file + sessionId + "_.csv");
				f.delete();
			} finally {
			}
			long lb = System.currentTimeMillis();
			System.out.println("Query lapsed time --> " + (lb - la) + " ms");
			Logger.debug(this, "uploadCsvData", "Query lapsed time --> " + (lb - la) + " ms", null, null);
			return resultValue("Success", "Uploaded virtually <br> Please click on save button to continue..");
		} else

		{
			return resultValue("Upload Failed",
					"You failed to upload " + file.getName() + " because the file was empty.");
		}

	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> uploadExcellData(MultipartFile file, HttpServletRequest request) {

		// String name = "Success";

		if (!file.isEmpty()) {
			System.out.println(file.getName());
			Logger.start(this, "uploadExcellData");
			long la = System.currentTimeMillis();
			String filePath = "C://temp//";
			String fileName = file.getName();
			try {

				if (System.getProperty("os.name").indexOf("Windows") > -1) {
					filePath = "C://DMW//Upload//";
				} else
				// If Unix
				{
					filePath = request.getSession().getServletContext().getRealPath("/");
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath += "/../apptemp/DMW/";
					System.out.println("***filepath****" + filePath);
				}
				File dir = new File(filePath);
				if (!dir.exists()) {
					if (dir.mkdir()) {
						System.out.println("Directory is created!");
					} else {
						System.out.println("Failed to create directory!");
					}
				}
				// reading the upload file.
				Logger.debug(this, "uploadExcellData", "filename : " + fileName + " ", null, null);
				InputStream inputStream = (InputStream) file.getInputStream();
				// FileOutputStream out_ = new FileOutputStream(filePath + file
				// + sessionId + "_.xls", false);
				DynamicBean dbean = new DynamicBean();
				ArrayList<LinkedHashMap<String, String>> al = new ArrayList<>();
				String[] headerArr = null;
				LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
						.getAttribute("TABLE_COLUMN_NAMES");
				LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
						.getAttribute("TABLE_COLUMN_TYPES");
				HashMap<String, String> hm = (HashMap<String, String>) request.getSession()
						.getAttribute("TABLE_SYSTEM_VALUES");
				HSSFWorkbook wb = new HSSFWorkbook(inputStream);
				HSSFSheet sheet = wb.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				int k = 0;
				/*
				 * while (rowIterator.hasNext()) { Row row = rowIterator.next();
				 * 
				 * // For each row, iterate through each columns Iterator<Cell>
				 * cellIterator = row.cellIterator(); while
				 * (cellIterator.hasNext()) {
				 * 
				 * Cell cell = cellIterator.next();
				 * 
				 * switch (cell.getCellType()) { case Cell.CELL_TYPE_BOOLEAN:
				 * System.out.print(cell.getBooleanCellValue() + "\t\t"); break;
				 * case Cell.CELL_TYPE_NUMERIC:
				 * System.out.print(cell.getNumericCellValue() + "\t\t"); break;
				 * case Cell.CELL_TYPE_STRING:
				 * System.out.print(cell.getStringCellValue() + "\t\t"); break;
				 * } } System.out.println(""); }
				 */
				// int rownum = sheet.getLastRowNum();
				while (rowIterator.hasNext()) {
					// HSSFRow row = sheet.getRow(i);
					Row row = rowIterator.next();
					if (row != null) {
						Iterator<Cell> cellIterator = row.cellIterator();
						Cell cell = null;
						try {
							if (k == 0) {
								headerArr = new String[row.getLastCellNum()];
								int j = 0;
								if (columnNames.size() == headerArr.length + 1) {
									while (cellIterator.hasNext()) {
										cell = cellIterator.next();
										String headerStr = "";
										switch (cell.getCellType()) {
										case Cell.CELL_TYPE_BOOLEAN:
											headerStr = cell.getBooleanCellValue() + "";
											break;
										case Cell.CELL_TYPE_NUMERIC:
											headerStr = cell.getNumericCellValue() + "";
											break;
										case Cell.CELL_TYPE_STRING:
											headerStr = cell.getStringCellValue();
											break;
										}
										if (columnNames.get(cell.getStringCellValue()) == null) {
											return resultValue("Upload Failed", "The file is not matching with Grid.");
										}
										headerArr[j] = headerStr;
										System.out.println(cell.getStringCellValue());
										j++;
									}
								} else {
									// return "Upload Failed , because
									// the
									// file is not matching with Grid.";
									return resultValue("Upload Failed", "The file is not matching with Grid.");
								}
								k++;
							} else {
								LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
								int j = 0;
								while (cellIterator.hasNext()) {
									cell = cellIterator.next();
									String data = "";
									switch (cell.getCellType()) {
									case Cell.CELL_TYPE_BOOLEAN:
										data = cell.getBooleanCellValue() + "";
										break;
									case Cell.CELL_TYPE_NUMERIC:
										data = cell.getNumericCellValue() + "";
										break;
									case Cell.CELL_TYPE_STRING:
										data = cell.getStringCellValue();
										break;
									}
									try {
										if (columnTypes.get(headerArr[j]).equalsIgnoreCase("number")) {
											// System.out.println(cell.getStringCellValue());
											if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
												lhm.put(headerArr[j], cell.getNumericCellValue() + "");
											else
												lhm.put(headerArr[j],
														Double.parseDouble(cell.getStringCellValue()) + "");
										} else if (columnTypes.get(headerArr[j]).equalsIgnoreCase("date")) {
											// System.out.println(row.getCell((short)
											// j).toString());
											if (hm.get("SYSTEM_DATE").equalsIgnoreCase(headerArr[j]))
												lhm.put(headerArr[j], "SYSDATE");
											else {
												SimpleDateFormat formatter = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm:ss");
												Date date = new Date();
												try {
													date = formatter.parse(cell.getStringCellValue());
												} catch (ParseException e) {
													formatter = new SimpleDateFormat("yyyy-MM-dd");
													date = formatter.parse(cell.getStringCellValue());
												}
												String pattern = "MM/dd/yyyy";
												SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
												// cell.setCellValue(simpleDateFormat.format(date));
												lhm.put(headerArr[j], simpleDateFormat.format(date));
											}
										} else
											lhm.put(headerArr[j], data);
									} catch (NumberFormatException e) {
										return resultValue("Upload Failed",
												"Please correct the data in file and then retry.<br>   <b>  Hint : Header '"
														+ headerArr[j] + "' @ Row :" + (k + 1)
														+ " should be Number</b>");
										// TODO: handle exception
									} catch (NullPointerException e) {
										return resultValue("Upload Failed",
												"Please correct the data in file and then retry.<br>   <b>  Hint : Header '"
														+ headerArr[j] + "' @ Row :" + (k + 1) + " cannot be null</b>");

									}
									// System.out.println(row.getCell((short)
									// j).toString());
									j++;
								}

								al.add(lhm);
								k++;
							}

						} catch (NumberFormatException nfe) {
							// continue;
						}

					}
				}
				System.out.println("upload size " + al.size());
				dbean.setData(al);
				// System.out.println(al.size());
				request.getSession().setAttribute("UPLOAD_DATA", dbean);
				// System.out.println(
				// "********************************************** " +
				// request.getParameter("append"));
				request.getSession().setAttribute("UPLOAD_TYPE", request.getParameter("append"));

			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(this, "uploadExcellData", "Exception :", null, e);

				throw new RuntimeException(e);
			} finally {

			}
			long lb = System.currentTimeMillis();
			System.out.println("Query lapsed time --> " + (lb - la) + " ms");
			Logger.debug(this, "uploadExcellData", "Query lapsed time --> " + (lb - la) + " ms", null, null);
			// return name;
			return resultValue("Success", "Uploaded virtually <br> Please click on save button to continue..");
		} else

		{
			// return "You failed to upload " + name + " because the file was
			// empty.";
			return resultValue("Upload Failed",
					"You failed to upload " + file.getName() + " because the file was empty.");
		}

	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> uploadExcellxData(MultipartFile file, HttpServletRequest request) {

		// String name = "Success";

		if (!file.isEmpty()) {
			System.out.println(file.getName());
			Logger.start(this, "uploadExcellData");
			long la = System.currentTimeMillis();
			String filePath = "C://temp//";
			String fileName = file.getName();
			try {

				if (System.getProperty("os.name").indexOf("Windows") > -1) {
					filePath = "C://DMW//Upload//";
				} else
				// If Unix
				{
					filePath = request.getSession().getServletContext().getRealPath("/");
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					filePath += "/../apptemp/DMW/";
					System.out.println("***filepath****" + filePath);
				}
				File dir = new File(filePath);
				if (!dir.exists()) {
					if (dir.mkdir()) {
						System.out.println("Directory is created!");
					} else {
						System.out.println("Failed to create directory!");
					}
				}
				// reading the upload file.
				Logger.debug(this, "uploadExcellData", "filename : " + fileName + " ", null, null);
				InputStream inputStream = (InputStream) file.getInputStream();
				// FileOutputStream out_ = new FileOutputStream(filePath + file
				// + sessionId + "_.xls", false);
				DynamicBean dbean = new DynamicBean();
				ArrayList<LinkedHashMap<String, String>> al = new ArrayList<>();
				String[] headerArr = null;
				LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
						.getAttribute("TABLE_COLUMN_NAMES");
				LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
						.getAttribute("TABLE_COLUMN_TYPES");
				HashMap<String, String> hm = (HashMap<String, String>) request.getSession()
						.getAttribute("TABLE_SYSTEM_VALUES");
				XSSFWorkbook wb = new XSSFWorkbook(inputStream);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				int k = 0;

				while (rowIterator.hasNext()) {
					// HSSFRow row = sheet.getRow(i);
					Row row = rowIterator.next();
					if (row != null) {
						Iterator<Cell> cellIterator = row.cellIterator();
						Cell cell = null;
						try {
							if (k == 0) {
								headerArr = new String[row.getLastCellNum()];
								int j = 0;
								if (columnNames.size() == headerArr.length + 1) {
									while (cellIterator.hasNext()) {
										cell = cellIterator.next();
										String headerStr = "";
										switch (cell.getCellType()) {
										case Cell.CELL_TYPE_BOOLEAN:
											headerStr = cell.getBooleanCellValue() + "";
											break;
										case Cell.CELL_TYPE_NUMERIC:
											headerStr = cell.getNumericCellValue() + "";
											break;
										case Cell.CELL_TYPE_STRING:
											headerStr = cell.getStringCellValue();
											break;
										}
										if (columnNames.get(cell.getStringCellValue()) == null) {
											return resultValue("Upload Failed", "The file is not matching with Grid.");
										}
										headerArr[j] = headerStr;
										System.out.println(cell.getStringCellValue());
										j++;
									}
								} else {
									// return "Upload Failed , because
									// the
									// file is not matching with Grid.";
									return resultValue("Upload Failed", "The file is not matching with Grid.");
								}
								k++;
							} else {
								LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
								int j = 0;
								while (cellIterator.hasNext()) {
									cell = cellIterator.next();
									String data = "";
									switch (cell.getCellType()) {
									case Cell.CELL_TYPE_BOOLEAN:
										data = cell.getBooleanCellValue() + "";
										break;
									case Cell.CELL_TYPE_NUMERIC:
										data = cell.getNumericCellValue() + "";
										break;
									case Cell.CELL_TYPE_STRING:
										data = cell.getStringCellValue();
										break;
									}
									try {
										if (columnTypes.get(headerArr[j]).equalsIgnoreCase("number")) {
											// System.out.println(cell.getStringCellValue());
											if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
												lhm.put(headerArr[j], cell.getNumericCellValue() + "");
											else
												lhm.put(headerArr[j],
														Double.parseDouble(cell.getStringCellValue()) + "");
										} else if (columnTypes.get(headerArr[j]).equalsIgnoreCase("date")) {
											// System.out.println(row.getCell((short)
											// j).toString());
											if (hm.get("SYSTEM_DATE").equalsIgnoreCase(headerArr[j]))
												lhm.put(headerArr[j], "SYSDATE");
											else {
												SimpleDateFormat formatter = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm:ss");
												Date date = new Date();
												try {
													date = formatter.parse(cell.getStringCellValue());
												} catch (ParseException e) {
													formatter = new SimpleDateFormat("yyyy-MM-dd");
													date = formatter.parse(cell.getStringCellValue());
												}
												String pattern = "MM/dd/yyyy";
												SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
												// cell.setCellValue(simpleDateFormat.format(date));
												lhm.put(headerArr[j], simpleDateFormat.format(date));
											}
										} else
											lhm.put(headerArr[j], data);
									} catch (NumberFormatException e) {
										return resultValue("Upload Failed",
												"Please correct the data in file and then retry.<br>   <b>  Hint : Header '"
														+ headerArr[j] + "' @ Row :" + (k + 1)
														+ " should be Number</b>");
										// TODO: handle exception
									} catch (NullPointerException e) {
										return resultValue("Upload Failed",
												"Please correct the data in file and then retry.<br>   <b>  Hint : Header '"
														+ headerArr[j] + "' @ Row :" + (k + 1) + " cannot be null</b>");

									}
									// System.out.println(row.getCell((short)
									// j).toString());
									j++;
								}

								al.add(lhm);
								k++;
							}
						} catch (NumberFormatException nfe) {
							// continue;
						}

					}
				}
				System.out.println("upload size " + al.size());
				dbean.setData(al);
				// System.out.println(al.size());
				request.getSession().setAttribute("UPLOAD_DATA", dbean);
				// System.out.println(
				// "********************************************** " +
				// request.getParameter("append"));
				request.getSession().setAttribute("UPLOAD_TYPE", request.getParameter("append"));

			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(this, "uploadExcellData", "Exception :", null, e);

				throw new RuntimeException(e);
			} finally {

			}
			long lb = System.currentTimeMillis();
			System.out.println("Query lapsed time --> " + (lb - la) + " ms");
			Logger.debug(this, "uploadExcellData", "Query lapsed time --> " + (lb - la) + " ms", null, null);
			// return name;
			return resultValue("Success", "Uploaded virtually <br> Please click on save button to continue..");
		} else

		{
			// return "You failed to upload " + name + " because the file was
			// empty.";
			return resultValue("Upload Failed",
					"You failed to upload " + file.getName() + " because the file was empty.");
		}

	}

	@SuppressWarnings("unchecked")
	public boolean saveUploadData(HttpServletRequest request) throws DMWException {
		boolean bool = false;
		if (request.getSession().getAttribute("UPLOAD_DATA") != null) {
			audit.insertMessageLog("saveUploadData", "Save upload data started", null, request.getRemoteUser(), null,
					null);
			// audit.insertAuditLog("saveUploadData", "Save upload data
			// started", "", request.getRemoteUser());
			DynamicBean dBean = (DynamicBean) request.getSession().getAttribute("UPLOAD_DATA");
			TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
			LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_TYPES");
			LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_NAMES");
			ArrayList<String> queryList = new ArrayList<>();
			HashMap<String, String> hm = (HashMap<String, String>) request.getSession()
					.getAttribute("TABLE_SYSTEM_VALUES");
			DMWDao dao = new DMWDao();
			if (((String) request.getSession().getAttribute("UPLOAD_TYPE")).equalsIgnoreCase("2")) {
				String filter = "   WHERE 1=1   ";
				String jsonLine = (String) request.getSession().getAttribute("TABLE_FILTERS");
				if (jsonLine != null) {
					JsonParser json = new JsonParser();
					JsonArray jArr = (JsonArray) json.parse(jsonLine);
					for (int j = 0; j < jArr.size(); j++) {
						JsonObject jObj = new JsonObject();
						jObj = (JsonObject) jArr.get(j);
						if (jObj.get("operator").getAsString().equalsIgnoreCase("gt")) {
							filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  >  "
									+ jObj.get("value").getAsString() + "";
						} else if (jObj.get("operator").getAsString().equalsIgnoreCase("lt")) {
							filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  <  "
									+ jObj.get("value").getAsString() + "";
						} else if (jObj.get("operator").getAsString().equalsIgnoreCase("eq")) {
							filter += "   AND " + (jObj.get("property").getAsString()).toLowerCase() + "  =  "
									+ jObj.get("value").getAsString() + "";
						} else {
							filter += "   AND UPPER(" + (jObj.get("property").getAsString()).toLowerCase() + ")  "
									+ jObj.get("operator").getAsString() + "  UPPER('" + jObj.get("value").getAsString()
									+ "%')";
						}
					}
				}
				queryList.add("DELETE FROM " + tableInfo.getOwner() + "." + tableInfo.getTableName() + filter);
				audit.insertMessageLog("frameUploadDeleteQuery", "Insert Query...", queryList.get(0),
						request.getRemoteUser(), null, null);
				dao.backupTableData(tableInfo, columnNames, request.getRemoteUser());
			} else {
				ArrayList<String> al = dao.getPrimaryKeyValues(tableInfo.getTableName(), tableInfo.getOwner(),
						request.getRemoteUser());
				if (al.size() > 0)
					queryList.addAll(frameDeleteQuery(dBean, tableInfo, columnTypes, al, request.getRemoteUser()));
			}
			queryList.addAll(frameUploadInsertQuery(dBean, tableInfo, columnTypes, hm, request.getRemoteUser()));
			bool = dao.executeTableSave(tableInfo, queryList, request.getRemoteUser());
			if (bool) {
				request.getSession().setAttribute("UPLOAD_DATA", null);
				dBean = new DynamicBean();
			}
			audit.insertMessageLog("saveUploadData", "Save upload data ended", null, request.getRemoteUser(), null,
					null);

		}
		return bool;
	}

	public HashMap<String, String> getSystemValues(TableInfo tableInfo, String user) throws DMWException {
		HashMap<String, String> map = new HashMap<String, String>();
		DMWDao dao = new DMWDao();
		map = dao.getSystemValues(tableInfo.getId(), user);
		return map;
	}

	public ArrayList<String> frameUploadInsertQuery(DynamicBean dBean, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes, HashMap<String, String> hm, String userId) {
		ArrayList<String> queryList = new ArrayList<>();
		try {
			Logger.info(this, "frameUploadInsertQuery", "reading the body...", null, null);
			for (int i = 0; i < dBean.getData().size(); i++) {
				// System.out.println(dBean.getData().get(i).keySet());
				StringBuffer query = new StringBuffer(
						"INSERT INTO " + tableInfo.getOwner() + "." + tableInfo.getTableName() + "(");
				for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
					// System.out.println(entry.getKey());
					if (!entry.getKey().equalsIgnoreCase("RID")) {
						query.append(entry.getKey() + ",");
					}
				}
				query.deleteCharAt(query.length() - 1);
				query.append(") VALUES (");
				for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
					if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
						if (hm.get("SYSTEM_DATE") != null && hm.get("SYSTEM_DATE").equalsIgnoreCase(entry.getKey())) {
							query.append("SYSDATE,");
						} else {

							String date = FormatUtil.formatDate(entry.getValue().toString().replaceAll("\"", ""));
							query.append("to_date('" + date + "','mm/dd/yyyy'),");
						}
					} else {
						if (hm.get("SYSTEM_USER") != null && hm.get("SYSTEM_USER").equalsIgnoreCase(entry.getKey())) {
							query.append("'" + userId + "',");
						} else {
							query.append("'" + entry.getValue().toString().replaceAll("'", "''").replaceAll("\"", "")
									+ "',");
						}
					}
				}

				query.deleteCharAt(query.length() - 1);
				query.append(")");
				// System.out.println(query);
				Logger.info(this, "frameUploadInsertQuery", "Insert Query..." + query, null, null);
				audit.insertMessageLog("frameUploadInsertQuery", "Insert Query...", query.toString(), userId, null,
						null);
				queryList.add(query.toString());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	public ArrayList<String> frameDeleteQuery(DynamicBean dBean, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes, ArrayList<String> al, String userId) {
		ArrayList<String> queryList = new ArrayList<>();
		try {
			Logger.info(this, "frameDeleteQuery", "reading the body...", null, null);
			for (int i = 0; i < dBean.getData().size(); i++) {
				// System.out.println(dBean.getData().get(i).keySet());
				StringBuffer query = new StringBuffer(
						"DELETE FROM  " + tableInfo.getOwner() + "." + tableInfo.getTableName() + " WHERE 1=1 ");
				for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
					// query.append(" AND "+entry.getKey() + "=" +
					// entry.getValue());
					for (String col : al) {
						if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
							String date = FormatUtil.formatDate(entry.getValue().toString().replaceAll("\"", ""));
							if (col.equalsIgnoreCase(entry.getKey())) {
								query.append(" AND " + entry.getKey() + "=");
								query.append("to_date('" + date + "','mm/dd/yyyy'),");
							}

						} else {
							if (col.equalsIgnoreCase(entry.getKey())) {
								query.append(" AND " + entry.getKey() + "='" + entry.getValue() + "'");
							}
						}
					}
				}
				Logger.info(this, "frameDeleteQuery", "Delete Query..." + query, null, null);
				// System.out.println("Delete Query..." + query);
				audit.insertMessageLog("frameDeleteQuery", "Delete Query...", query.toString(), userId, null, null);
				queryList.add(query.toString());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryList;
	}

	public ViewException viewException(HttpServletRequest request) throws DMWException {
		Logger.start(this, "viewException");
		DMWDao dao = new DMWDao();
		ViewException vExp = dao.viewException(request.getRemoteUser());
		Logger.end(this, "viewException");
		return vExp;
	}

	@SuppressWarnings("unchecked")
	public void download(HttpServletRequest request, HttpServletResponse response, String type) {
		try {
			Logger.start(this, "downloadAll");
			String fileNAme = "";
			DynamicBean dBean = null;
			String jsonLine = (String) request.getSession().getAttribute("TABLE_FILTERS");
			TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
			LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_NAMES");
			LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_TYPES");

			DMWDao dao = new DMWDao();
			if (type.equalsIgnoreCase("csv")) {
				dBean = dao.getTableDetails(jsonLine, tableInfo, columnNames, columnTypes);
				fileNAme = downloadCsv(dBean, request);
			} else if (type.equalsIgnoreCase("all")) {
				dBean = getTableAllDetails(request);
				fileNAme = downloadCsv(dBean, request);
			} else if (type.equalsIgnoreCase("xls")) {
				// dBean = getTableDetails(request);
				dBean = dao.getTableDetails(jsonLine, tableInfo, columnNames, columnTypes);
				fileNAme = downloadXls(dBean, request);
			} else if (type.equalsIgnoreCase("xlsx")) {
				// dBean = getTableDetails(request);
				dBean = dao.getTableDetails(jsonLine, tableInfo, columnNames, columnTypes);
				fileNAme = downloadXlsx(dBean, request);
			} else if (type.equalsIgnoreCase("xlsx_all")) {
				dBean = getTableAllDetails(request);
				fileNAme = downloadXlsx(dBean, request);
			}
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();

			// construct the complete absolute path of the file
			String fullPath = fileNAme;
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			// System.out.println("MIME type: " + mimeType);

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();
			downloadFile = new File(fullPath);
			downloadFile.delete();
			Logger.end(this, "downloadAll");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return fileName;
	}

	public DynamicBean getTableAllDetails(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableAllDetails");
		DMWDao dao = new DMWDao();
		DynamicBean dBean = null;
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		int startRec = 0;
		int endRec = tableInfo.getEndRec();
		if (request.getParameter("start") != null) {
			startRec = Integer.parseInt(request.getParameter("start"));
		}
		if (request.getParameter("limit") != null) {
			endRec = startRec + Integer.parseInt(request.getParameter("limit"));
		}
		String sort = request.getParameter("sort");
		String dir = request.getParameter("dir");
		// parsing json Object
		String jsonLine = request.getParameter("filter");
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> columnNames = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_NAMES");

		if (tableInfo != null) {
			tableInfo.setStartRec(startRec);
			tableInfo.setEndRec(endRec);
			if (sort != null)
				tableInfo.setSortBy(sort);
			if (dir != null)
				tableInfo.setSortDir(dir);
			if (request.getParameter("pivot") != null && request.getParameter("pivot").equalsIgnoreCase("true")) {
				tableInfo.setPivot(true);
			}

			dBean = dao.getTableAllDetails(jsonLine, tableInfo, columnNames);
		}
		Logger.end(this, "getTableDetails");
		return dBean;
	}

	public String downloadXls(DynamicBean dBean, HttpServletRequest request) {

		Logger.start(this, "downloadXls");
		Logger.debug(this, "downloadXls", "Started the download...", null, null);
		String fileName = null;
		Calendar cal = Calendar.getInstance();
		try {
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_TYPES");
			TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
			fileName = "DMW" + "_" + tableInfo.getTableName() + "_" + cal.getTimeInMillis() + ".xls";
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");
			fileName = appPath + "/" + fileName;
			try {
				FileOutputStream fileOut = new FileOutputStream(fileName);
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet(tableInfo.getTableName());
				HSSFCellStyle cellStyle = wb.createCellStyle();

				HSSFFont font = wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setColor(HSSFColor.WHITE.index);
				cellStyle.setFont(font);
				/*
				 * cellStyle.setBorderBottom((short) 1);
				 * cellStyle.setBorderLeft((short) 1);
				 * cellStyle.setBorderRight((short) 1);
				 * cellStyle.setBorderTop((short) 1);
				 */
				cellStyle.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
				cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				HSSFCellStyle cellStyleNew = wb.createCellStyle();

				/*
				 * cellStyleNew.setBorderBottom((short) 1);
				 * cellStyleNew.setBorderLeft((short) 1);
				 * cellStyleNew.setBorderRight((short) 1);
				 * cellStyleNew.setBorderTop((short) 1);
				 */
				cellStyleNew.setFillForegroundColor(HSSFColor.WHITE.index);
				cellStyleNew.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				HSSFCellStyle cellStyleAlt = wb.createCellStyle();

				/*
				 * cellStyleAlt.setBorderBottom((short) 1);
				 * cellStyleAlt.setBorderLeft((short) 1);
				 * cellStyleAlt.setBorderRight((short) 1);
				 * cellStyleAlt.setBorderTop((short) 1);
				 */
				cellStyleAlt.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
				cellStyleAlt.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

				HSSFCell cell = null;
				HSSFRow row = sheet.createRow((short) (0));
				int k = 0;
				for (int i = 0; i < dBean.getData().size(); i++) {
					if (i == 0) {
						for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
							if (!entry.getKey().equalsIgnoreCase("RID")) {
								cell = row.createCell((short) (k++));
								cell.setCellValue(entry.getKey());
								cell.setCellStyle(cellStyle);
							}
						}
					}
					k = 0;
					row = sheet.createRow((short) (i + 1));
					for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							cell = row.createCell((short) (k++));
							if (columnTypes.get(entry.getKey()).equalsIgnoreCase("number"))
								// cell.setCellValue(Double.parseDouble(entry.getValue()));
								if (entry.getValue() != null)
								cell.setCellValue(Double.parseDouble(entry.getValue()));
								else
								cell.setCellValue("");
							else if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
								if (entry.getValue() != null && !entry.getValue().equalsIgnoreCase("")) {
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									// System.out.println(formatter.parse("19611015"));
									Date date = new Date();
									try {
										date = formatter.parse(entry.getValue());
									} catch (ParseException e) {
										formatter = new SimpleDateFormat("yyyy-MM-dd");
										date = formatter.parse(entry.getValue());
									}
									String pattern = "MM/dd/yyyy";
									SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
									cell.setCellValue(simpleDateFormat.format(date));
								} else {
									cell.setCellValue(entry.getValue());
								}
							} else
								cell.setCellValue(entry.getValue());
							if (i % 2 == 1) {
								cell.setCellStyle(cellStyleAlt);
							} else {
								cell.setCellStyle(cellStyleNew);
							}
						}
					}
				}
				wb.write(fileOut);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(this, "Download", "Exception :", null, e);
			} finally {
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(this, "downloadXls", "Exception :", null, e);
		} finally {

		}
		Logger.debug(this, "downloadXls", "Ended the download...", null, null);
		Logger.end(this, "downloadXls");
		return fileName;

	}

	@SuppressWarnings("deprecation")
	public String downloadXlsx(DynamicBean dBean, HttpServletRequest request) {

		Logger.start(this, "downloadXlsx");
		Logger.debug(this, "downloadXlsx", "Started the download...", null, null);
		String fileName = null;
		Calendar cal = Calendar.getInstance();
		try {
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
					.getAttribute("TABLE_COLUMN_TYPES");
			TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
			fileName = "DMW" + "_" + tableInfo.getTableName() + "_" + cal.getTimeInMillis() + ".xlsx";
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");
			fileName = appPath + "/" + fileName;
			try {

				XSSFWorkbook wb = new XSSFWorkbook();
				XSSFSheet sheet = (XSSFSheet) wb.createSheet(tableInfo.getTableName());
				/* Create an object of type XSSFTable */
				XSSFTable myTable = sheet.createTable();

				CTTable cttable = myTable.getCTTable();
				/* Let us define the required Style for the table */
				CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
				table_style.setName("TableStyleMedium14");

				/* Set Table Style Options */
				table_style.setShowColumnStripes(false); // showColumnStripes=0
				table_style.setShowRowStripes(true); // showRowStripes=1

				/* Define the data range including headers */
				AreaReference my_data_range = new AreaReference(new CellReference(0, 0),
						new CellReference(dBean.getData().size(), columnTypes.size() - 2));

				/* Set Range to the Table */
				cttable.setRef(my_data_range.formatAsString());
				cttable.setDisplayName("MYTABLE"); // this is the display name
													// of the table
				cttable.setName("Test"); // This maps to "displayName" attribute
											// in <table>, OOXML
				cttable.setId(1L); // id attribute against table as long value

				CTTableColumns columns = cttable.addNewTableColumns();
				columns.setCount(columnTypes.size() - 1); // define number of
															// columns

				/* Define Header Information for the Table */
				for (int i = 0; i < columnTypes.size() - 1; i++) {
					CTTableColumn column = columns.addNewTableColumn();
					column.setName("Column" + i);
					column.setId(i + 1);
				}
				XSSFRow row = sheet.createRow(0);
				XSSFCell localXSSFCell = row.createCell(0);
				int k = 0;
				for (int i = 0; i < dBean.getData().size(); i++) {
					if (i == 0) {
						for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
							if (!entry.getKey().equalsIgnoreCase("RID")) {
								localXSSFCell = row.createCell(k++);
								localXSSFCell.setCellValue(entry.getKey());
							}
						}
					}
					k = 0;
					row = sheet.createRow((i + 1));
					for (Map.Entry<String, String> entry : dBean.getData().get(i).entrySet()) {
						if (!entry.getKey().equalsIgnoreCase("RID")) {
							localXSSFCell = row.createCell(k++);
							if (columnTypes.get(entry.getKey()).equalsIgnoreCase("number")) {
								if (entry.getValue() != null)
									localXSSFCell.setCellValue(Double.parseDouble(entry.getValue()));
								else
									localXSSFCell.setCellValue("");
							} else if (columnTypes.get(entry.getKey()).equalsIgnoreCase("date")) {
								if (entry.getValue() != null && !entry.getValue().equalsIgnoreCase("")) {
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									// System.out.println(formatter.parse("19611015"));
									Date date = new Date();
									try {
										date = formatter.parse(entry.getValue());
									} catch (ParseException e) {
										formatter = new SimpleDateFormat("yyyy-MM-dd");
										date = formatter.parse(entry.getValue());
									}
									String pattern = "MM/dd/yyyy";
									SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
									localXSSFCell.setCellValue(simpleDateFormat.format(date));
								} else {
									localXSSFCell.setCellValue(entry.getValue());
								}
							} else
								localXSSFCell.setCellValue(entry.getValue());
						}
					}
				}
				FileOutputStream fileOut = new FileOutputStream(fileName);
				wb.write(fileOut);
				wb.close();
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(this, "Download", "Exception :", null, e);
			} finally {
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(this, "downloadXlsx", "Exception :", null, e);
		} finally {

		}
		Logger.debug(this, "downloadXlsx", "Ended the download...", null, null);
		Logger.end(this, "downloadXlsx");
		return fileName;

	}

	public boolean validateVersion(HttpServletRequest request) throws DMWException {
		Logger.start(this, "validateVersion");
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		DMWDao dao = new DMWDao();
		boolean bool = dao.getVersion(tableInfo);
		Logger.end(this, "validateVersion");
		return bool;

	}

	public String saveFavourites(HttpServletRequest request) throws DMWException {
		Logger.start(this, "saveFavourites");
		String filters = (String) request.getSession().getAttribute("TABLE_FILTERS");
		/*
		 * if (filters == null) { return "empty"; }
		 */
		DMWDao dao = new DMWDao();
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		String str = dao.saveFavourites(tableInfo.getId() + "", filters, request.getRemoteUser());
		Logger.end(this, "saveFavourites");
		return str;
	}

	public List<SimpleComboBean> getColumnsList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getColumnsList");
		DMWDao dao = new DMWDao();
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		List<SimpleComboBean> list = dao.getEditableColumns(tableInfo);
		Logger.end(this, "getColumnsList");
		return list;
	}

	public boolean updateBulkData(HttpServletRequest request) throws DMWException {
		Logger.start(this, "updateTableDetails");
		boolean bool = false;
		TableInfo tableInfo = (TableInfo) request.getSession().getAttribute("TableInfo");
		Logger.info(this, "updateTableDetails", "got tableInfo from session...", null, null);
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_TYPES");
		Logger.info(this, "updateTableDetails", "got columnTypes from session...", null, null);
		ArrayList<String> queryList = new ArrayList<>();
		queryList.add(framePivotBulkUpdateQuery(request, tableInfo, columnTypes));
		DMWDao dao = new DMWDao();
		// bool = dao.executeTableSave(queryList, request.getRemoteUser());
		bool = dao.executeTableSave(tableInfo, queryList, request.getRemoteUser());
		Logger.debug(this, "updateTableDetails", "Ended of Query framing...", null, null);
		Logger.end(this, "updateTableDetails");
		return bool;
	}

	public String framePivotBulkUpdateQuery(HttpServletRequest request, TableInfo tableInfo,
			LinkedHashMap<String, String> columnTypes) throws DMWException {
		Logger.info(this, "framePivotBulkUpdateQuery", "reading the body...", null, null);
		String jsonLine = (String) request.getSession().getAttribute("TABLE_FILTERS");
		StringBuffer query = new StringBuffer("UPDATE " + tableInfo.getOwner() + "." + tableInfo.getTableName());
		StringBuffer bkpQuery = new StringBuffer("select ");
		if (tableInfo != null && tableInfo.isPivot()) {
			if (columnTypes.get(tableInfo.getPivotData()).equalsIgnoreCase("date")) {
				query.append("   SET " + tableInfo.getPivotData() + "=to_date('" + request.getParameter("values-name")
						+ "','mm/dd/yyyy')  ");
			} else {
				query.append("   SET " + tableInfo.getPivotData() + "='" + request.getParameter("values-name") + "'  ");
			}
			bkpQuery.append(tableInfo.getPivotData() + " FROM ");
			bkpQuery.append(tableInfo.getOwner() + "." + tableInfo.getTableName());
			bkpQuery.append(
					" WHERE " + tableInfo.getPivotColumns() + "='" + request.getParameter("column-name") + "'   ");
			query.append(" WHERE " + tableInfo.getPivotColumns() + "='" + request.getParameter("column-name") + "'   ");

		} else {
			if (columnTypes.get(request.getParameter("column-name")).equalsIgnoreCase("date")) {
				query.append("   SET " + request.getParameter("column-name") + "=to_date('"
						+ request.getParameter("values-name") + "','mm/dd/yyyy')  ");
			} else {
				query.append("   SET " + request.getParameter("column-name") + "='"
						+ request.getParameter("values-name") + "'");
			}
			bkpQuery.append(tableInfo.getPivotData() + " FROM ");
			bkpQuery.append(tableInfo.getOwner() + "." + tableInfo.getTableName());
			bkpQuery.append(" WHERE 1 = 1 ");
			query.append(" WHERE 1 = 1 ");
		}
		if (jsonLine != null) {
			JsonParser json = new JsonParser();
			JsonArray jArr = (JsonArray) json.parse(jsonLine);
			for (int i = 0; i < jArr.size(); i++) {
				JsonObject jObj = new JsonObject();
				jObj = (JsonObject) jArr.get(i);
				String str = "";
				String oper = (jObj.get("operator").getAsString());
				if (jObj.get("operator").getAsString().equalsIgnoreCase("like")) {
					str = "%";
					query.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  " + oper + " UPPER('"
							+ jObj.get("value").getAsString() + str + "')");
					bkpQuery.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  " + oper + " UPPER('"
							+ jObj.get("value").getAsString() + str + "')");
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("eq")) {
					query.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  = "
							+ jObj.get("value").getAsString() + str + "");
					bkpQuery.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  = "
							+ jObj.get("value").getAsString() + str + "");
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("gt")) {
					query.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  > "
							+ jObj.get("value").getAsString() + str + "");
					bkpQuery.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  > "
							+ jObj.get("value").getAsString() + str + "");
				} else if (jObj.get("operator").getAsString().equalsIgnoreCase("lt")) {
					query.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  < "
							+ jObj.get("value").getAsString() + str + "");
					bkpQuery.append(" AND UPPER(" + (jObj.get("property").getAsString()) + ")  < "
							+ jObj.get("value").getAsString() + str + "");
				}

			}
		}
		DMWDao dao = new DMWDao();
		ArrayList<String> al = dao.getOldValues(bkpQuery.toString(), request.getRemoteUser());
		for (String mess : al) {
			audit.insertMessageLog("updateBulkData", query.toString(), "", request.getRemoteUser(), mess,
					request.getParameter("values-name"));
		}
		Logger.info(this, "framePivotBulkUpdateQuery", "Update Query..." + query.toString(), null, null);
		return query.toString();

	}

	@SuppressWarnings("unchecked")
	public String getColumnInfo(HttpServletRequest request) {
		Logger.start(this, "getColumnInfo");
		LinkedHashMap<String, String> columnTypes = (LinkedHashMap<String, String>) request.getSession()
				.getAttribute("TABLE_COLUMN_TYPES");
		if (columnTypes != null) {
			return columnTypes.get(request.getParameter("selected"));
		}
		Logger.end(this, "getColumnInfo");
		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getUserCount(HttpServletRequest request) {
		ServletContext application = request.getServletContext();
		// String returnValue = "Testing";
		boolean bool = false;
		HashMap<String, String> hm = new HashMap<>();
		try {
			if (request.getSession().getAttribute("CURRENT_USERS") != null) {
				hm = (HashMap<String, String>) request.getSession().getAttribute("CURRENT_USERS");
				bool = true;
			}
			// System.out.println(request.getParameter("tableName"));
			// hm.put(request.getRemoteUser(), "Current User");
			Enumeration<String> attrEnum = application.getAttributeNames();
			while (attrEnum.hasMoreElements()) {
				String name = (String) attrEnum.nextElement();

				// Get the value of the attribute
				Object value = application.getAttribute(name);

				if (value instanceof Integer) {
					// System.out.println(value);
					// System.out.println(name + "|" + hm.get(name));
					if (request.getParameter("tableName").equalsIgnoreCase(value + "")) {
						if (hm.get(name) == null) {
							hm.put(name, "New User");
						} else if (hm.get(name).equalsIgnoreCase("New User")) {
							hm.put(name, "Current User");
						} else if (hm.get(name).equalsIgnoreCase("User Left")) {
							hm.remove(name);
						} /*
							 * else { hm.put(name, "User Left"); }
							 */
					} else if (bool) {
						if (hm.get(name) == null) {

						} else if (hm.get(name).equalsIgnoreCase("Current User")) {
							hm.put(name, "User Left");
						} else if (hm.get(name).equalsIgnoreCase("User Left")) {
							hm.remove(name);
						}
					}
				}
			}
			hm.put(request.getRemoteUser(), "Current User");
			request.getSession().setAttribute("CURRENT_USERS", hm);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// System.out.println(returnValue);
		return hm;

	}

	public String getCurrentUserList(HttpServletRequest request) {
		ServletContext application = request.getServletContext();
		String returnValue = "";
		// System.out.println(request.getParameter("tableName"));
		Enumeration<String> attrEnum = application.getAttributeNames();
		while (attrEnum.hasMoreElements()) {
			String name = (String) attrEnum.nextElement();

			// Get the value of the attribute
			Object value = application.getAttribute(name);

			if (value instanceof Integer) {
				System.out.println(value);
				if (((Integer) value).intValue() == Integer.parseInt(request.getParameter("tableName")))
					returnValue += "<b>" + name + "</b><br>";
			}
		}
		return returnValue;
	}

	public HashMap<String, String> resultValue(String key, String value) {
		HashMap<String, String> hm = new HashMap<>();
		if (key != null)
			hm.put(key, value);
		return hm;
	}
}
