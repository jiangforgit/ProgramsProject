package com.vcom.uploadfile.domain;

public class TableUploadLog {

	private String FILE_PATH = "";
	private String IP = "";
	private int PORT;
	private String FILE_URL = "";//文件对应的浏览路径 前缀
	private int UPLOAD_TYPE;//1:udp 服务器地址  2:http服务器地址
	private int START_POSITION;//文件断点位置
	private int UPLOAD_COUNT;//该文件在该台服务器上传的次数
	private String CREATE_TIME = "";
	
	public String getFILE_PATH() {
		return FILE_PATH;
	}
	public void setFILE_PATH(String fILE_PATH) {
		FILE_PATH = fILE_PATH;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public int getPORT() {
		return PORT;
	}
	public void setPORT(int pORT) {
		PORT = pORT;
	}
	public String getFILE_URL() {
		return FILE_URL;
	}
	public void setFILE_URL(String fILE_URL) {
		FILE_URL = fILE_URL;
	}
	public int getUPLOAD_TYPE() {
		return UPLOAD_TYPE;
	}
	public void setUPLOAD_TYPE(int uPLOAD_TYPE) {
		UPLOAD_TYPE = uPLOAD_TYPE;
	}
	public int getSTART_POSITION() {
		return START_POSITION;
	}
	public void setSTART_POSITION(int sTART_POSITION) {
		START_POSITION = sTART_POSITION;
	}
	public int getUPLOAD_COUNT() {
		return UPLOAD_COUNT;
	}
	public void setUPLOAD_COUNT(int uPLOAD_COUNT) {
		UPLOAD_COUNT = uPLOAD_COUNT;
	}
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
}
