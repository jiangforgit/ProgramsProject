package com.vcom.uploadfile.domain;

public class TableTaskContent{
	private long id;
	private String packetId = "";
    private String busiType = "";//媒体属于哪个大类别下 如 核保--健康---保全
    private String busiSeqDes = "";//大类别下的子类别
	private int contentType;
	private String contentName = "";
	private String contentValue = "";//若为媒体文件则保存其对应的网络url
	private String orderReason = "";
	private int startPosition;
    private int uploadStatus;//上传状态 -2:文件不存在 -1:离线图片暂不上传 0： 默认状态（插入数据库） 1：正在上传    2：上传成功 3:Busi指令响应确认
    private int fileLength;//单位字节
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
