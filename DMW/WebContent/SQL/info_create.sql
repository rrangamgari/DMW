DROP TABLE I2WEBDATA.DMW_TABLE_INFO ;

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
  DMW_ROLE           CHAR(1 CHAR)               DEFAULT 'N',
  VERSION            INTEGER                    DEFAULT 1
);


--  There is no statement for index I2WEBDATA.SYS_C003013300.
--  The object is created when the parent object is created.

ALTER TABLE I2WEBDATA.DMW_TABLE_INFO ADD (
  PRIMARY KEY
  (DMW_TABLE_INFO_ID)
  USING INDEX
    TABLESPACE MPPCTSTD01
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          256K
                NEXT             256K
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
                BUFFER_POOL      DEFAULT
                FLASH_CACHE      DEFAULT
                CELL_FLASH_CACHE DEFAULT
               )
  ENABLE VALIDATE);

ALTER TABLE I2WEBDATA.DMW_TABLE_INFO ADD (
  CONSTRAINT DMW_TABLE_CONF_FK 
  FOREIGN KEY (DMW_TABLE_CONF_ID) 
  REFERENCES I2WEBDATA.DMW_TABLE_CONF (DMW_TABLE_CONF_ID)
  ENABLE VALIDATE);

GRANT ALL ON I2WEBDATA.DMW_TABLE_INFO TO I2WEB;
