create table VCOM_ORDER  (
   ID                   INTEGER PRIMARY KEY AUTOINCREMENT,
   ORDER_ID             VARCHAR(50),
   ORDER_TYPE           VARCHAR(25),
   ORDER_MSG            VARCHAR(50),
   TASK_ID              VARCHAR(50),
   ITASK_ID             VARCHAR(50),
   BUSY_TYPE            VARCHAR(50),
   BUSY_SUB_TYPE        VARCHAR(50),
   MARK                 NUMBER(1)		              default 0,
   MARKRESULT           NUMBER(1)		              default 0,
   STATE                NUMBER(1)		              default 0,
   CREATE_TIME          DATE
);
CREATE TABLE  VCOM_ORDER_CONTENT  (
   ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
   PACKET_ID           VARCHAR NOT NULL,
   ORDER_REASON        VARCHAR,
   CONTENT_TYPE        INTEGER,
   CONTENT_NAME        VARCHAR,
   CONTENT_VALUE       VARCHAR,
   START_POSITION      INTEGER, 
   FILE_LENGTH         INTEGER,
   UPLOAD_STATUS       INTEGER,
   MARK                NUMBER(1)		              default 0,
   MARKRESULT          NUMBER(1)		              default 0,
   ORDER_ID            VARCHAR,
   CREATE_TIME         DATE,
   FILE_ROOT_URL       VARCHAR,
   IS_SHOW             INTEGER                        default 0,
   THREAD_ID           VARCHAR,
   SORT                NUMBER(1)		              default 0,
   UPDATE_TIME         INTEGER                        default 0
);
CREATE TABLE  VCOM_UPLOAD_URL  (
   ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
   IP                  VARCHAR,
   PORT                INTEGER,
   FILE_URL            VARCHAR,
   UPLOAD_COUNT        INTEGER,
   UPLOAD_TYPE         INTEGER,
   CREATE_TIME         DATE,
   IS_DIE             INTEGER                          default 0
);
CREATE TABLE  VCOM_UPLOAD_LOG  (
   ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
   FILE_PATH           VARCHAR,
   IP                  VARCHAR,
   PORT                INTEGER,
   FILE_URL            VARCHAR,
   UPLOAD_TYPE         INTEGER,
   START_POSITION      INTEGER,
   UPLOAD_COUNT        INTEGER,
   CREATE_TIME         DATE
);
CREATE TABLE VCOM_SENDING_LOG(
   PACKET_ID           VARCHAR PRIMARY KEY,
   MSG                 VARCHAR,
   MARK                INTEGER,
   MARKRESULT          INTEGER,
   VAR1                INTEGER,
   VAR2                INTEGER,
   VAR3                VARCHAR,
   VAR4                VARCHAR,
   CREATETIME          DATE
);
CREATE TABLE VCOM_MENUS(
   ID                  VARCHAR PRIMARY KEY,
   MENU_VALUE          VARCHAR,
   PID                 VARCHAR,
   HAS_CHILD           INTEGER,
   VAR1                INTEGER,
   VAR2                INTEGER,
   VAR3                VARCHAR,
   VAR4                VARCHAR
);
CREATE TABLE VCOM_UNCAUGHT_EXCEPTION (
   ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
   FILE_PATH           VARCHAR,
   CREATE_TIME         VARCHAR,
   IS_UPLOADED         INTEGER
);
CREATE TABLE VCOM_LOCATION_TEST (
   ID                  INTEGER PRIMARY KEY AUTOINCREMENT,
   LON                 VARCHAR,
   LAT                 VARCHAR,
   ADDRESS             VARCHAR,
   FLAG                VARCHAR,
   CREATE_TIME         DATE
);
CREATE TABLE uploadTest (id INTEGER PRIMARY KEY AUTOINCREMENT,threadName VARCHAR,fileName VARCHAR,fileLength INTEGER,guid VARCHAR,cmd VARCHAR,startPosition INTEGER,packetges INTEGER,failurePackets VARCHAR);