CREATE TABLE I2WEBDATA.DMW_TABLE_CONF
(
  DMW_TABLE_CONF_ID  NUMBER,
  CONF_NAME          VARCHAR2(100 BYTE),
  PARENT_ID          NUMBER                     DEFAULT 0
)
;

--  There is no statement for index I2WEBDATA.SYS_C003013294.
--  The object is created when the parent object is created.

ALTER TABLE I2WEBDATA.DMW_TABLE_CONF ADD (
  PRIMARY KEY
  (DMW_TABLE_CONF_ID)
  );

GRANT ALL ON I2WEBDATA.DMW_TABLE_CONF TO I2WEB;



CREATE TABLE I2WEBDATA.DMW_TABLE_INFO
(
  DMW_TABLE_INFO_ID  NUMBER,
  DMW_TABLE_CONF_ID  NUMBER,
  TABLE_OWNER        VARCHAR2(100 CHAR),
  TABLE_ALIAS        VARCHAR2(100 CHAR),
  TABLE_NAME         VARCHAR2(100 CHAR),
  SCHEMA_NAME        VARCHAR2(50 CHAR),
  CONDITIONAL_QUERY  CHAR(1 CHAR)               DEFAULT 'N'                   NOT NULL,
  PIVOT              CHAR(1 CHAR)               DEFAULT 'N'                   NOT NULL,
  PAGINATION         CHAR(1 CHAR)               DEFAULT 'N'                   NOT NULL,
  DMW_ROLE           CHAR(1 CHAR)               DEFAULT 'N'
)
;


--  There is no statement for index I2WEBDATA.SYS_C003013300.
--  The object is created when the parent object is created.

ALTER TABLE I2WEBDATA.DMW_TABLE_INFO ADD (
  PRIMARY KEY
  (DMW_TABLE_INFO_ID))
  ;

ALTER TABLE I2WEBDATA.DMW_TABLE_INFO ADD (
  CONSTRAINT DMW_TABLE_CONF_FK 
  FOREIGN KEY (DMW_TABLE_CONF_ID) 
  REFERENCES I2WEBDATA.DMW_TABLE_CONF (DMW_TABLE_CONF_ID)
  ENABLE VALIDATE);

GRANT ALL ON I2WEBDATA.DMW_TABLE_INFO TO I2WEB;

CREATE SEQUENCE I2WEBDATA.DMW_TABLE_INFO_SEQ
  START WITH 121
  MAXVALUE 9999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;


GRANT ALL ON I2WEBDATA.DMW_TABLE_INFO_SEQ TO I2WEB;
/*Table 3*/
CREATE TABLE I2WEBDATA.DMW_TABLE_ACCESS
(
  DMW_TABLE_INFO_ID  NUMBER,
  ADD_ROW            CHAR(1 BYTE)               DEFAULT 'N'                   NOT NULL,
  DELETE_ROW         CHAR(1 BYTE)               DEFAULT 'N'                   NOT NULL,
  MASS_UPDATE        CHAR(1 BYTE)               DEFAULT 'N'                   NOT NULL,
  UPLOAD             CHAR(1 BYTE)               DEFAULT 'N'                   NOT NULL,
  DOWNLOAD           CHAR(1 BYTE)               DEFAULT 'N'                   NOT NULL,
  EDITABLE           CHAR(1 BYTE)               DEFAULT 'N',
  DMW_ROLE_ID        NUMBER
)
;

ALTER TABLE I2WEBDATA.DMW_TABLE_ACCESS ADD (
  CONSTRAINT DMW_ROLE_ID_FK 
  FOREIGN KEY (DMW_ROLE_ID) 
  REFERENCES I2WEBDATA.DMW_ROLES (DMW_ROLE_ID)
  ENABLE VALIDATE,
  CONSTRAINT DMW_TABLE_ROLES_FK 
  FOREIGN KEY (DMW_TABLE_INFO_ID) 
  REFERENCES I2WEBDATA.DMW_TABLE_INFO (DMW_TABLE_INFO_ID)
  ENABLE VALIDATE);

GRANT ALL ON I2WEBDATA.DMW_TABLE_ACCESS TO I2WEB;

CREATE TABLE I2WEBDATA.DMW_TABLE_EDITABLE
(
  DMW_TABLE_INFO_ID  NUMBER,
  COLUMN_NAME        VARCHAR2(100 BYTE),
  ALLOW_EDITABLE     CHAR(1 BYTE)               DEFAULT 'N',
  DMW_ROLE_ID        NUMBER
)
;

GRANT ALL I2WEBDATA.DMW_TABLE_EDITABLE TO I2WEB;

CREATE TABLE I2WEBDATA.DMW_AUDIT_TRAIL
(
  EVENT_NAME         VARCHAR2(100 BYTE),
  EVENT_DESCRIPTION  VARCHAR2(1000 BYTE),
  EVENT_QUERY        VARCHAR2(1000 BYTE),
  UPDATED_BY         VARCHAR2(100 BYTE),
  UPDATED_DATE       DATE
)
;


GRANT ALL ON I2WEBDATA.DMW_AUDIT_TRAIL TO I2WEB;

CREATE TABLE I2WEBDATA.DMW_CONDITIONAL_QUERY
(
  DMW_TABLE_INFO_ID  NUMBER,
  QUERY              VARCHAR2(500 BYTE)
)
;


GRANT ALL ON I2WEBDATA.DMW_CONDITIONAL_QUERY TO I2WEB;



CREATE TABLE I2WEBDATA.DMW_ROLES
(
  DMW_ROLE_ID   NUMBER,
  ROLE_NAME     VARCHAR2(100 BYTE),
  CREATED_BY    VARCHAR2(100 BYTE),
  CREATED_DATE  DATE
)
;


--  There is no statement for index I2WEBDATA.SYS_C003013310.
--  The object is created when the parent object is created.

ALTER TABLE I2WEBDATA.DMW_ROLES ADD (
  PRIMARY KEY
  (DMW_ROLE_ID)
  ;

GRANT ALL ON I2WEBDATA.DMW_ROLES TO I2WEB;


CREATE TABLE I2WEBDATA.DMW_TABLE_AUTO
(
  DMW_TABLE_INFO_ID  NUMBER,
  COLUMN_NAME        VARCHAR2(200 BYTE),
  AUTO_FIELD         VARCHAR2(200 BYTE)
)
;


GRANT ALL ON I2WEBDATA.DMW_TABLE_AUTO TO I2WEB;


CREATE TABLE I2WEBDATA.DMW_TABLE_PAGING
(
  DMW_TABLE_INFO_ID  NUMBER,
  PAGE_LENGTH        NUMBER,
  SORT_BY            VARCHAR2(50 BYTE),
  SORT_DIR           VARCHAR2(5 BYTE)
)
;


ALTER TABLE I2WEBDATA.DMW_TABLE_PAGING ADD (
  CONSTRAINT DMW_TABLE_PAGING_FK 
  FOREIGN KEY (DMW_TABLE_INFO_ID) 
  REFERENCES I2WEBDATA.DMW_TABLE_INFO (DMW_TABLE_INFO_ID)
  ENABLE VALIDATE);

GRANT ALL ON I2WEBDATA.DMW_TABLE_PAGING TO I2WEB;

CREATE TABLE I2WEBDATA.DMW_TABLE_PIVOT
(
  DMW_TABLE_INFO_ID  NUMBER,
  FIXED_FIELDS       VARCHAR2(100 BYTE),
  PIVOT_COLUMNS      VARCHAR2(100 BYTE),
  PIVOT_DATA         VARCHAR2(100 BYTE)
)
;


ALTER TABLE I2WEBDATA.DMW_TABLE_PIVOT ADD (
  CONSTRAINT DMW_TABLE_PIVOT_FK 
  FOREIGN KEY (DMW_TABLE_INFO_ID) 
  REFERENCES I2WEBDATA.DMW_TABLE_INFO (DMW_TABLE_INFO_ID)
  ENABLE VALIDATE);

GRANT ALL ON I2WEBDATA.DMW_TABLE_PIVOT TO I2WEB;

CREATE TABLE I2WEBDATA.DMW_TABLE_ROLES
(
  DMW_TABLE_INFO_ID  NUMBER,
  DMW_ROLE_ID        NUMBER
)
;


GRANT ALL ON I2WEBDATA.DMW_TABLE_ROLES TO I2WEB;

CREATE TABLE I2WEBDATA.DMW_USER_ROLES
(
  DMW_ROLE_ID   NUMBER,
  EMPLOYEE_NO   VARCHAR2(100 BYTE),
  CREATED_BY    VARCHAR2(100 BYTE),
  CREATED_DATE  DATE
)
;


GRANT ALL ON I2WEBDATA.DMW_USER_ROLES TO I2WEB;


CREATE TABLE DMW_TABLE_FAVOURITE
(
   DMW_TABLE_INFO_ID   NUMBER,
   USER_NAME         VARCHAR2 (200),
   COLUMN_NAME         VARCHAR2 (200),
   COLUMN_OPERATOR            VARCHAR2 (20),
   COLUMN_VALUE               VARCHAR2 (200)
);

GRANT ALL ON DMW_TABLE_FAVOURITE TO i2web;