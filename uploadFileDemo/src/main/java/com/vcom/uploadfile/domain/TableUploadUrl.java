package com.vcom.uploadfile.domain;

public class TableUploadUrl {

	private String IP = "";
	private int PORT;
	private String FILE_URL = "";//文件对应的浏览路径 前缀
	private int UPLOAD_COUNT;//当前ip 上传过的文件总数
	private int UPLOAD_TYPE;//1:udp 服务器地址  2:http服务器地址
	private String CREATE_TIME = "";
	private int IS_DIE;
	
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
	public int getUPLOAD_COUNT() {
		return UPLOAD_COUNT;
	}
	public void setUPLOAD_COUNT(int uPLOAD_COUNT) {
		UPLOAD_COUNT = uPLOAD_COUNT;
	}
	public int getUPLOAD_TYPE() {
		return UPLOAD_TYPE;
	}
	public void setUPLOAD_TYPE(int uPLOAD_TYPE) {
		UPLOAD_TYPE = uPLOAD_TYPE;
	}
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public int getIS_DIE() {
		return IS_DIE;
	}
	public void setIS_DIE(int iS_DIE) {
		IS_DIE = iS_DIE;
	}
}
