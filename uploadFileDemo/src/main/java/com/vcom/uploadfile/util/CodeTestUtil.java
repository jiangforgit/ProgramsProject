package com.vcom.uploadfile.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import com.vcom.uploadfile.db.WorkOrderIUDS;
import android.content.Context;
import android.util.Log;

public class CodeTestUtil {

	private static final String Tag = "CodeTestUtil";
	
	//文件上传log测试
	public static void testSendData(Context context,String threadName,byte[] data,byte[] packetData){
//		Log.i(Tag, "testSendData");
		String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 0, 2));
		String md5Name = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 2, 16));
		int fileLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 18, 4));
		String guid = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 22, 16));
		int startPosition = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 38, 4));
		int packetges = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 42, 4));
		int index = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 46, 4));
		int packetgeLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 50, 4));
		int fileContentLength = BytesDealUtil.subBytes(data, 54, packetgeLen).length;
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		iuds.uploadFileTest(threadName, md5Name,fileLen, guid, "up", startPosition, index, "");//printHexString(packetData)
	}
	
	public static String printHexString( byte[] b) { 
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++) { 
			builder.append(Integer.toHexString(b[i] & 0xFF));
		} 
		return builder.toString();
	}
	
	public static void testAskingData(Context context,String threadName,byte[] data){
//		Log.i(Tag, "testAskingData");
		String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 0, 2));
		String md5Name = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 2, 16));
		int fileLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 18, 4));
		String guid = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 22, 16));
		int startPosition = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 38, 4));
		int packetges = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 42, 4));
//		int taskTypeLen =  BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 46, 4));
//		String taskType = new String(BytesDealUtil.subBytes(data, 50, taskTypeLen));
//		int fileTypeLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 50+taskTypeLen, 4));
//		String fileType = new String(BytesDealUtil.subBytes(data, 54+taskTypeLen, fileTypeLen));
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		iuds.uploadFileTest(threadName, md5Name,fileLen, guid, "asking", startPosition, packetges, "");
	}
	
	public static void testAskingResponseData(Context context,String threadName,byte[] data){
//		Log.i(Tag, "testAskingResponseData");
		String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 0, 2));
		String md5Name = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 2, 16));
		int fileLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 18, 4));
		String guid = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(data, 22, 16));
		int startPosition = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 38, 4));
		int responseContentLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 42, 4));
		String responseContent = new String(BytesDealUtil.subBytes(data, 46, responseContentLen));
//		int taskTypeLen =  BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 46+responseContentLen, 4));
//		String taskType = new String(BytesDealUtil.subBytes(data, 50+responseContentLen, taskTypeLen));
//		int fileTypeLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(data, 50+responseContentLen+taskTypeLen, 4));
//		String fileType = new String(BytesDealUtil.subBytes(data, 54+responseContentLen+taskTypeLen, fileTypeLen));
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		iuds.uploadFileTest(threadName, md5Name,fileLen, guid, "down", startPosition, 0, responseContent);
	}
	
	public static void testFileComplete(Context context,String threadName,String MD5Name){
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		iuds.uploadFileTest(threadName, MD5Name,0, "", "文件上传成功", 0, 0, "");
	}
}
