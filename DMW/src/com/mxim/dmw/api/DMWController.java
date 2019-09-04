package com.mxim.dmw.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mxim.dmw.datamanager.DMWAdminManager;
import com.mxim.dmw.datamanager.DMWManager;
import com.mxim.dmw.domain.DynamicBean;
import com.mxim.dmw.domain.SimpleComboBean;
import com.mxim.dmw.domain.TableList;
import com.mxim.dmw.domain.UserInfo;
import com.mxim.dmw.domain.ViewException;
import com.mxim.dmw.exception.DMWException;
import com.mxim.dmw.util.Logger;

@Controller
@RequestMapping("/")
public class DMWController {

	public DMWController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = "/echo/{message}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody EchoMessage echo(@PathVariable String message, ModelMap model) {
		System.out.println("ABBUIControler.echo() message=" + message);
		return new EchoMessage(message);
	}

	class EchoMessage {
		public String message;

		public EchoMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	@RequestMapping(value = "/uidefaults", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody EchoMessage uidefaults(HttpSession session) {

		return new EchoMessage("testing");
	}

	@RequestMapping(value = "/getUser", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody UserInfo getUser(HttpServletRequest request) throws Exception {
		Logger.start(this, "getUser");
		UserInfo user = null;
		// String userId = (String)
		// WebContextManager.get().getRequest().getRemoteUser();
		DMWManager manager = new DMWManager();
		System.out.println(request.getRemoteUser());
		user = manager.getUser(request);
		Logger.end(this, "getUser");
		return user;
	}

	@RequestMapping(value = "/getTableInfo", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DynamicBean getTableInfo(String tableId, HttpServletRequest request) throws Exception {
		Logger.start(this, "getTableInfo");
		String json = "";
		DMWManager manager = new DMWManager();
		DynamicBean dBean;
		dBean = manager.getTableInfo(tableId, request);
		// System.out.println(request.getSession().getLastAccessedTime());
		System.out.println(json);
		Logger.end(this, "getTableInfo");
		return dBean;
	}

	@RequestMapping(value = "/getTableDetails", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DynamicBean getTableDetails(String tableId, HttpServletRequest request) throws Exception {
		Logger.start(this, "getTableDetails");
		DMWManager manager = new DMWManager();
		DynamicBean dBean = manager.getTableDetails(request);
		Logger.end(this, "getTableDetails");
		return dBean;
	}

	// @ExceptionHandler(DMWException.class)
	@RequestMapping(value = "/saveTableDetails", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody boolean saveTableDetails(HttpServletRequest request) throws DMWException {
		Logger.start(this, "saveTableDetails");
		DMWManager manager = new DMWManager();
		boolean bool = false;
		if (request.getParameter("create").equalsIgnoreCase("true"))
			bool = manager.insertTableDetails(request);
		else
			bool = manager.updateTableDetails(request);
		Logger.end(this, "saveTableDetails");
		return bool;
	}

	@RequestMapping(value = "/deleteTableData", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody boolean deleteTableData(HttpServletRequest request) throws DMWException {
		Logger.start(this, "deleteTableData");
		DMWManager manager = new DMWManager();
		boolean bool = false;
		bool = manager.deleteTableData(request);
		Logger.end(this, "deleteTableData");
		return bool;
	}

	@RequestMapping(value = "/getTableList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<TableList> getTableList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableList");
		DMWManager manager = new DMWManager();
		ArrayList<TableList> dBean = manager.getParentNameList(request);
		Logger.end(this, "getTableList");
		return dBean;
	}

	@RequestMapping(value = "/downloadAll", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void downloadAll(HttpServletRequest request, HttpServletResponse response)
			throws DMWException {
		Logger.start(this, "downloadAll");
		DMWManager manager = new DMWManager();
		manager.download(request, response, "All");
		Logger.end(this, "downloadAll");
		// return fileName;
	}

	@RequestMapping(value = "/downloadCsv", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void downloadCsv(HttpServletRequest request, HttpServletResponse response)
			throws DMWException {
		Logger.start(this, "downloadCsv");
		DMWManager manager = new DMWManager();
		manager.download(request, response, "csv");
		Logger.end(this, "downloadCsv");
		// return fileName;
	}

	@RequestMapping(value = "/downloadXls", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void downloadXls(HttpServletRequest request, HttpServletResponse response)
			throws DMWException {
		Logger.start(this, "downloadXls");
		DMWManager manager = new DMWManager();
		manager.download(request, response, "xls");
		Logger.end(this, "downloadXls");
		// return fileName;
	}

	@RequestMapping(value = "/downloadXlsx", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void downloadXlsx(HttpServletRequest request, HttpServletResponse response)
			throws DMWException {
		Logger.start(this, "downloadXlsx");
		DMWManager manager = new DMWManager();
		manager.download(request, response, "xlsx");
		Logger.end(this, "downloadXlsx");
		// return fileName;
	}

	@RequestMapping(value = "/downloadXlsxAll", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void downloadXlsxAll(HttpServletRequest request, HttpServletResponse response)
			throws DMWException {
		Logger.start(this, "downloadXlsxAll");
		DMWManager manager = new DMWManager();
		manager.download(request, response, "xlsx_all");
		Logger.end(this, "downloadXlsxAll");
		// return fileName;
	}

	@RequestMapping(value = "/uploadExcel", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody HashMap<String, String> uploadExcellData(@RequestParam("file") MultipartFile file,
			HttpServletRequest request) {

		Logger.start(this, "uploadExcellData");
		HashMap<String, String> hm = new HashMap<>();
		DMWManager manager = new DMWManager();
		if (!file.isEmpty()) {
			if (file.getOriginalFilename().endsWith("csv")) {
				hm = manager.uploadCsvData(file, request);
			} else if (file.getOriginalFilename().endsWith("xls")) {
				try {
					hm = manager.uploadExcellData(file, request);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (file.getOriginalFilename().endsWith("xlsx")) {
				try {
					hm = manager.uploadExcellxData(file, request);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(hm.size() + "|");
		// hm.put("Testing", "Testing bvalue");
		Logger.end(this, "uploadExcellData");
		return hm;

	}

	@RequestMapping(value = "/validateVersion", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody boolean validateVersion(HttpServletRequest request) throws DMWException {
		Logger.start(this, "validateVersion");
		DMWManager manager = new DMWManager();
		boolean bool = manager.validateVersion(request);
		Logger.end(this, "validateVersion");
		return bool;
	}

	@RequestMapping(value = "/saveUploadData", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody boolean saveUploadData(HttpServletRequest request) throws DMWException {
		Logger.start(this, "saveUploadData");
		DMWManager manager = new DMWManager();
		boolean bool = manager.saveUploadData(request);
		Logger.end(this, "saveUploadData");
		return bool;
	}

	@RequestMapping(value = "/viewException", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ViewException viewException(HttpServletRequest request) throws DMWException {
		Logger.start(this, "viewException");
		DMWManager manager = new DMWManager();
		ViewException exp = manager.viewException(request);
		Logger.end(this, "viewException");
		return exp;
	}

	@RequestMapping(value = "/getConfList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<SimpleComboBean> getConfList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getConfList");
		DMWAdminManager manager = new DMWAdminManager();
		ArrayList<SimpleComboBean> dBean = manager.getConfList(request);
		Logger.end(this, "getConfList");
		return dBean;
	}

	@RequestMapping(value = "/getRolesList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<SimpleComboBean> getRolesList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getRolesList");
		DMWAdminManager manager = new DMWAdminManager();
		List<SimpleComboBean> dBean = manager.getRolesList(request);
		Logger.end(this, "getRolesList");
		return dBean;
	}

	@RequestMapping(value = "/getTableOwners", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<SimpleComboBean> getTableOwners(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTableOwners");
		DMWAdminManager manager = new DMWAdminManager();
		List<SimpleComboBean> dBean = manager.getTableOwners(request);
		Logger.end(this, "getTableOwners");
		return dBean;
	}

	@RequestMapping(value = "/getTablesList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<SimpleComboBean> getTablesList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getTablesList");
		DMWAdminManager manager = new DMWAdminManager();
		List<SimpleComboBean> dBean = manager.getTablesList(request);
		Logger.end(this, "getTablesList");
		return dBean;
	}

	@RequestMapping(value = "/getColumnsList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<SimpleComboBean> getColumnsList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getColumnsList");
		DMWAdminManager manager = new DMWAdminManager();
		List<SimpleComboBean> dBean = manager.getColumnsList(request);
		Logger.end(this, "getColumnsList");
		return dBean;
	}

	@RequestMapping(value = "/getEditColumnsList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<SimpleComboBean> getEditColumnsList(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getEditColumnsList");
		DMWManager manager = new DMWManager();
		List<SimpleComboBean> dBean = manager.getColumnsList(request);
		Logger.end(this, "getEditColumnsList");
		return dBean;
	}

	@RequestMapping(value = "/saveNewTableInfo", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody boolean saveNewTableInfo(HttpServletRequest request) throws DMWException {
		Logger.start(this, "saveNewTableInfo");
		DMWAdminManager manager = new DMWAdminManager();
		boolean bool = false;
		bool = manager.saveNewTableInfo(request);
		Logger.end(this, "saveNewTableInfo");
		return bool;
	}

	@RequestMapping(value = "/updateBulkData", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody boolean updateBulkData(HttpServletRequest request) throws DMWException {
		Logger.start(this, "updateBulkData");
		DMWManager manager = new DMWManager();
		boolean bool = false;
		bool = manager.updateBulkData(request);
		Logger.end(this, "updateBulkData");
		return bool;
	}

	@RequestMapping(value = "/saveFavourites", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String saveFavourites(HttpServletRequest request) throws DMWException {
		Logger.start(this, "saveFavourites");
		DMWManager manager = new DMWManager();
		String str = manager.saveFavourites(request);
		Logger.end(this, "saveFavourites");
		return str;
	}

	@RequestMapping(value = "/getColumnInfo", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getColumnInfo(HttpServletRequest request) throws DMWException {
		Logger.start(this, "getColumnInfo");
		DMWManager manager = new DMWManager();
		String str = manager.getColumnInfo(request);
		Logger.end(this, "getColumnInfo");
		return str;
	}

	@RequestMapping(value = "/getUserCount", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody HashMap<String, String> getUserCount(HttpServletRequest request) {
		Logger.start(this, "getUserCount");
		DMWManager manager = new DMWManager();
		HashMap<String, String> hm = manager.getUserCount(request);
		Logger.end(this, "getUserCount");
		return hm;
	}

	@RequestMapping(value = "/getCurrentUserList", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getCurrentUserList(HttpServletRequest request) {
		Logger.start(this, "getCurrentUserList");
		DMWManager manager = new DMWManager();
		String str = manager.getCurrentUserList(request);
		Logger.end(this, "getCurrentUserList");
		return str;
	}
}
