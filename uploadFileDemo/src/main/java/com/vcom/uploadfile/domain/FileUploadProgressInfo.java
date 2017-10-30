package com.vcom.uploadfile.domain;

public class FileUploadProgressInfo {

	private String fileName;
	
	private int fileLength;
	
	private int uploadedLength;
	
	private int uploadStatus;

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileLength() {
		return fileLength;
	}

	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}

	public int getUploadedLength() {
		return uploadedLength;
	}

	public void setUploadedLength(int uploadedLength) {
		this.uploadedLength = uploadedLength;
	}

	public int getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
}
