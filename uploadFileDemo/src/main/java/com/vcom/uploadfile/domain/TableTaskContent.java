package com.vcom.uploadfile.domain;

public class TableTaskContent{
	private long id;
	private String packetId = "";
    private String busiType = "";//ý�������ĸ�������� �� �˱�--����---��ȫ
    private String busiSeqDes = "";//������µ������
	private int contentType;
	private String contentName = "";
	private String contentValue = "";//��Ϊý���ļ��򱣴����Ӧ������url
	private String orderReason = "";
	private int startPosition;
    private int uploadStatus;//�ϴ�״̬ -2:�ļ������� -1:����ͼƬ�ݲ��ϴ� 0�� Ĭ��״̬���������ݿ⣩ 1�������ϴ�    2���ϴ��ɹ� 3:Busiָ����Ӧȷ��
    private int fileLength;//��λ�ֽ�
    private String orderId = "";
    private int mark;
    private int markResult;
    private String date = "";
    private String fileRootUrl = "";
    private String THREAD_ID = "";
	private int SORT;
	private long UPDATE_TIME;
    
	public TableTaskContent(){
		super();
	}
   
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPacketId() {
		return packetId;
	}

	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getBusiSeqDes() {
		return busiSeqDes;
	}

	public void setBusiSeqDes(String busiSeqDes) {
		this.busiSeqDes = busiSeqDes;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getContentValue() {
		return contentValue;
	}

	public void setContentValue(String contentValue) {
		this.contentValue = contentValue;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public int getFileLength() {
		return fileLength;
	}

	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public int getMarkResult() {
		return markResult;
	}

	public void setMarkResult(int markResult) {
		this.markResult = markResult;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFileRootUrl() {
		return fileRootUrl;
	}

	public void setFileRootUrl(String fileRootUrl) {
		this.fileRootUrl = fileRootUrl;
	}

	public String getTHREAD_ID() {
		return THREAD_ID;
	}

	public void setTHREAD_ID(String tHREAD_ID) {
		THREAD_ID = tHREAD_ID;
	}

	public int getSORT() {
		return SORT;
	}

	public void setSORT(int sORT) {
		SORT = sORT;
	}

	public long getUPDATE_TIME() {
		return UPDATE_TIME;
	}

	public void setUPDATE_TIME(long uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}

	public String getOrderReason() {
		return orderReason;
	}

	public void setOrderReason(String orderReason) {
		this.orderReason = orderReason;
	}
	
}
