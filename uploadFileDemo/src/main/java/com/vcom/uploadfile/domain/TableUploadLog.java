package com.vcom.uploadfile.domain;

public class TableUploadLog {

	private String FILE_PATH = "";
	private String IP = "";
	private int PORT;
	private String FILE_URL = "";//�ļ���Ӧ�����·�� ǰ׺
	private int UPLOAD_TYPE;//1:udp ��������ַ  2:http��������ַ
	private int START_POSITION;//�ļ��ϵ�λ��
	private int UPLOAD_COUNT;//���ļ��ڸ�̨�������ϴ��Ĵ���
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
