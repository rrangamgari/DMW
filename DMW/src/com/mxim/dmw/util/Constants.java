package com.mxim.dmw.util;

public class Constants {
	public static final String DMW_ROLES_QUERY = "SELECT *    FROM I2WEBDATA.DMW_ROLES   ORDER BY DMW_ROLE_ID";

	public static final String DMW_ALL_OWNERS_QUERY = "SELECT OWNER, RANK () OVER (ORDER BY owner) AS rowum  FROM (SELECT DISTINCT OWNER    FROM ALL_TAB_COLUMNS ORDER BY OWNER)";

	public static final String DMW_ALL_TABLES_QUERY = "SELECT TABLE_NAME, RANK () OVER (ORDER BY TABLE_NAME) AS rowum  FROM (SELECT DISTINCT TABLE_NAME    FROM ALL_TAB_COLUMNS   WHERE  owner = ':OWNER'   AND TABLE_NAME LIKE ':TABLE_NAME%')";

	public static final String DMW_TABLES_SEQ_QUERY = "SELECT I2WEBDATA.DMW_TABLE_INFO_SEQ.NEXTVAL FROM DUAL";

	public static final String DMW_TABLES_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_INFO (DMW_TABLE_INFO_ID,"
			+ "                                      DMW_TABLE_CONF_ID,"
			+ "                                      TABLE_OWNER,"
			+ "                                      TABLE_ALIAS,                                      TABLE_NAME,"
			+ "                                      SCHEMA_NAME,"
			+ "                                      CONDITIONAL_QUERY,"
			+ "                                      PIVOT,                                      PAGINATION,"
			+ "                                                                            DMW_ROLE)"
			+ "     VALUES (':DMW_TABLE_INFO_ID',             ':DMW_TABLE_CONF_ID',             ':TABLE_OWNER',"
			+ "             ':TABLE_ALIAS',             ':TABLE_NAME',             ':SCHEMA_NAME',"
			+ "             ':CONDITIONAL_QUERY',             ':PIVOT',             ':PAGINATION',"
			+ "                       ':DMW_ROLE')";
	public static final String DMW_TABLES_ROLES_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_ROLES     VALUES (?, ?)";

	public static final String DMW_TABLE_ACCESS_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_ACCESS (DMW_TABLE_INFO_ID,ADD_ROW,"
			+ "                              DELETE_ROW,                              MASS_UPDATE,"
			+ "                              UPLOAD,                              DOWNLOAD,"
			+ "                              DMW_ROLE_ID,                              EDITABLE)"
			+ "     VALUES (:DMW_TABLE_INFO_ID,  :ADD_ROW,             :DELETE_ROW,             :MASS_UPDATE,"
			+ "             :UPLOAD,             :DOWNLOAD,             :DMW_ROLE_ID,             :EDITABLE)";

	public static final String DMW_ALL_COLUMNS_QUERY = "SELECT  COLUMN_NAME  ,COLUMN_ID  FROM ALL_TAB_COLUMNS   WHERE  OWNER = ':OWNER'   AND TABLE_NAME = ':TABLE_NAME' ORDER BY COLUMN_NAME";

	public static final String DMW_TABLES_QUERY_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_CONDITIONAL_QUERY(DMW_TABLE_INFO_ID,QUERY) VALUES (?,?)";

	public static final String DMW_TABLES_PAGING_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_PAGING(DMW_TABLE_INFO_ID,PAGE_LENGTH,SORT_BY,SORT_DIR) VALUES (?,?,?,?)";

	public static final String DMW_TABLES_PIVOT_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_PIVOT(DMW_TABLE_INFO_ID,FIXED_FIELDS,PIVOT_COLUMNS,PIVOT_DATA) VALUES (?,?,?,?)";

	public static final String DMW_TABLE_EDITABLE_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_EDITABLE(DMW_TABLE_INFO_ID,COLUMN_NAME,ALLOW_EDITABLE,DMW_ROLE_ID) VALUES (?,?,?,?)";

	public static final String DMW_TABLES_AUTO_INSERT_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_AUTO(DMW_TABLE_INFO_ID,COLUMN_NAME,AUTO_FIELD) VALUES (?,?,?)";

	// Non Admin Queries
	public static final String DMW_CONF_LIST_QUERY = "SELECT *    FROM I2WEBDATA.DMW_TABLE_CONF  ORDER BY parent_id";
	
	public static final String DMW_CONF_LIST_TREE_QUERY="SELECT DISTINCT a.*"
			+ "      FROM I2WEBDATA.DMW_TABLE_CONF a"
			+ "  START WITH DMW_TABLE_CONF_ID IN"
			+ "              (SELECT DISTINCT DMW_TABLE_CONF_ID"
			+ "                 FROM I2WEBDATA.DMW_TABLE_INFO"
			+ "                WHERE (   DMW_ROLE = 'N'"
			+ "                       OR DMW_TABLE_INFO_ID IN"
			+ "                             (SELECT DMW_TABLE_INFO_ID"
			+ "                                FROM I2WEBDATA.dmw_table_roles"
			+ "                               WHERE DMW_ROLE_ID IN (:ROLES))))"
			+ "   CONNECT BY PRIOR parent_id = dmw_table_conf_id"
			+ "  ORDER BY CONF_NAME";

	public static final String DMW_TABLES_NAME_LIST_QUERY = "SELECT *  FROM I2WEBDATA.DMW_TABLE_INFO"
			+ " WHERE (   DMW_ROLE = 'N'        OR DMW_TABLE_INFO_ID IN (SELECT DMW_TABLE_INFO_ID"
			+ "                                   FROM I2WEBDATA.dmw_table_roles"
			+ "                                  WHERE DMW_ROLE_ID IN (:ROLES))) ORDER BY DMW_TABLE_CONF_ID, DMW_TABLE_INFO_ID";
	public static final String DMW_TABLES_INFORMATION_QUERY = "SELECT m.*,       p.PAGE_LENGTH,"
			+ "       p.SORT_BY,       p.SORT_DIR,       pi.FIXED_FIELDS,       pi.PIVOT_COLUMNS,"
			+ "       pi.PIVOT_DATA,       CASE WHEN r.DMW_ROLE_ID = pr.DMW_ROLE_ID THEN r.ADD_ROW ELSE 'N' END"
			+ "          AS ADD_ROW,       CASE"
			+ "          WHEN r.DMW_ROLE_ID = pr.DMW_ROLE_ID THEN r.DELETE_ROW          ELSE 'N'"
			+ "       END          AS DELETE_ROW,       CASE"
			+ "          WHEN r.DMW_ROLE_ID = pr.DMW_ROLE_ID THEN r.MASS_UPDATE          ELSE 'N'       END"
			+ "          AS MASS_UPDATE,       CASE WHEN r.DMW_ROLE_ID = pr.DMW_ROLE_ID THEN r.UPLOAD ELSE 'N' END"
			+ "          AS UPLOAD,       CASE WHEN r.DMW_ROLE_ID = pr.DMW_ROLE_ID THEN r.DOWNLOAD ELSE 'N' END"
			+ "          AS DOWNLOAD,       CASE WHEN r.DMW_ROLE_ID = pr.DMW_ROLE_ID THEN r.editable ELSE 'N' END"
			+ "          AS editable,       CASE WHEN f.count > 0  THEN 'Y' ELSE 'N' END"
			+ "          AS FAVOURITE  FROM I2WEBDATA.DMW_TABLE_INFO m,       I2WEBDATA.DMW_TABLE_PAGING p,"
			+ "       I2WEBDATA.DMW_TABLE_PIVOT pi,       (SELECT *          FROM I2WEBDATA.DMW_TABLE_ACCESS"
			+ "         WHERE DMW_ROLE_ID IN (:DMW_TABLE_ROLES_ID)) r,       I2WEBDATA.DMW_TABLE_ROLES pr,"
			+ "       (SELECT COUNT (*) count          FROM I2WEBDATA.DMW_TABLE_FAVOURITE"
			+ "         WHERE DMW_TABLE_INFO_ID = :DMW_TABLE_INFO_ID  AND USER_NAME=':USER_NAME'"
			+ ") f WHERE     m.DMW_TABLE_INFO_ID = p.DMW_TABLE_INFO_ID(+)"
			+ "       AND m.DMW_TABLE_INFO_ID = pi.DMW_TABLE_INFO_ID(+)"
			+ "       AND m.DMW_TABLE_INFO_ID = r.DMW_TABLE_INFO_ID(+)"
			+ "       AND m.DMW_TABLE_INFO_ID = pr.DMW_TABLE_INFO_ID(+)"
			+ "       AND m.DMW_TABLE_INFO_ID = :DMW_TABLE_INFO_ID";

	public static final String DMW_EDITABLE_COLUMNS_QUERY = "SELECT DISTINCT         custom.column_name,"
			+ "         cons.owner,         (CASE"
			+ "             WHEN cols.column_name = custom.column_name THEN 'N'             ELSE ALLOW_EDITABLE"
			+ "          END)            AS ALLOW_EDITABLE    FROM all_constraints cons,"
			+ "         all_cons_columns cols,         (SELECT *            FROM i2webdata.DMW_TABLE_EDITABLE"
			+ "           WHERE DMW_TABLE_INFO_ID = :TABLE_ID) custom"
			+ "   WHERE     UPPER (cols.table_name) = UPPER (':TABLE_NAME')         AND cons.constraint_type = 'P'"
			+ "         AND UPPER (cons.owner) = UPPER (':TABLE_OWNER')"
			+ "         AND cons.constraint_name = cols.constraint_name"
			+ "         AND cons.owner = cols.owner  AND custom.column_name IS NOT NULL"
			+ "    ORDER BY custom.column_name";

	public static final String DMW_CONDITIONAL_QUERY = " SELECT * FROM I2WEBDATA.DMW_CONDITIONAL_QUERY where DMW_TABLE_INFO_ID=:DMW_TABLE_INFO_ID";

	public static final String DMW_TABLE_FAVOURITE_QUERY = " SELECT * FROM I2WEBDATA.DMW_TABLE_FAVOURITE where DMW_TABLE_INFO_ID=:DMW_TABLE_INFO_ID and USER_NAME=':USER_NAME'";

	public static final String DMW_FAVOURITES_SAVE_QUERY = "INSERT INTO I2WEBDATA.DMW_TABLE_FAVOURITE (   DMW_TABLE_INFO_ID   , USER_NAME         ,   COLUMN_NAME   ,   COLUMN_OPERATOR,"
			+ "   COLUMN_VALUE ) VALUES (		   ?,		   ?,		   ?,		   ?,		   ?		)";

	public static final String DMW_FAVOURITES_DELETE_QUERY = "DELETE FROM I2WEBDATA.DMW_TABLE_FAVOURITE WHERE   DMW_TABLE_INFO_ID = :DMW_TABLE_INFO_ID   AND USER_NAME = ':USER_NAME'";

	public static final String DMW_SYSTEM_VALUES_QUERY = "select * from I2WEBDATA.DMW_TABLE_AUTO where DMW_TABLE_INFO_ID=:DMW_TABLE_INFO_ID";
	
	public static final String DMW_PRIMARY_KEY_VALUES_QUERY="SELECT distinct cols.Column_name"
			+ "  FROM all_constraints cons, all_cons_columns cols"
			+ " WHERE     UPPER (cols.table_name) = UPPER (':TABLE_NAME')"
			+ "       AND cons.constraint_type = 'P'"
			+ "       AND UPPER (cons.owner) = UPPER (':TABLE_OWNER')"
			+ "       AND cons.constraint_name = cols.constraint_name"
			+ "       AND cons.owner = cols.owner";
}
