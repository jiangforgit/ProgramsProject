package com.vcom.uploadfile.interfaces;

import android.content.Context;

public interface IUploadFile {

    //�ļ��ϴ�
	void uploadFile(Context context,String orderId,String packetId, String filePath,String orderReason,String contentValue,String taskType,String progressAction,String account,String version,int contentType,String fileType,boolean isOffline);
	
	//�ļ��ش�
	void reUploadFile(Context context,String taskType,String progressAction,String account,String version,String fileType);
}
