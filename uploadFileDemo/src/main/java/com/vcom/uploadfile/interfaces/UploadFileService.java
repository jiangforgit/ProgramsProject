package com.vcom.uploadfile.interfaces;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.vcom.uploadfile.db.WorkOrderIUDS;
import com.vcom.uploadfile.domain.TableTaskContent;
import com.vcom.uploadfile.enums.EnumContentType;
import com.vcom.uploadfile.manager.ThreadPollsManager;
import com.vcom.uploadfile.thread.FileWriteThread;
import com.vcom.uploadfile.thread.HttpFileUploadThread;
public class UploadFileService implements IUploadFile{

	private final String Tag = "UploadFileService";
	private static UploadFileService instance;
	public static boolean isReuploading = false;
//	private String fileUploadMode = "";//文件上传途径  udp 、http 、tcp
//	public static final int socketListNum = 30;//限制最大的socket数
//	private static List<DatagramSocket> socketList;//socket 链接池
	
	private UploadFileService(){
		
	}
	
	//get instance
	public static UploadFileService getInstance(){
		if(instance == null){
			instance = new UploadFileService();
		}
		return instance;
	}
	
	//文件上传途径  udp 、http 、tcp
	private int fileUploadMode(Context context){
		SharedPreferences sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		return Integer.parseInt(sp.getString("FILEUPLOADMODE", "0"));
	}
	
	public void setUploadSocketTimeOut(Context context,int socketTimeOut){
		SharedPreferences verify_sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		Editor verify_et = verify_sp.edit();
		verify_et.putInt("UPLOAD_SOCKET_TIME_OUT", socketTimeOut);
		verify_et.commit();
	}
	
	public void setUploadReadTimeOut(Context context,int readTimeOut){
		SharedPreferences verify_sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		Editor verify_et = verify_sp.edit();
		verify_et.putInt("UPLOAD_READ_TIME_OUT", readTimeOut);
		verify_et.commit();
	}
	
	public int getUploadSocketTimeOut(Context context){
		SharedPreferences sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		return sp.getInt("UPLOAD_SOCKET_TIME_OUT", 3000);
	}
	
	public int getUploadReadTimeOut(Context context){
		SharedPreferences sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		return sp.getInt("UPLOAD_READ_TIME_OUT", 5000);
	}
	
	@Override
	public void uploadFile(Context context,String orderId,String packetId, String filePath,String orderReason,String contentValue,String taskType,String progressAction,String account,String version,int contentType,String fileType,boolean isOffline) {
		// TODO Auto-generated method stub
		if(new File(filePath).exists()){
			int fileUploadMode = fileUploadMode(context);
			if(fileUploadMode==0 || fileUploadMode == 2){
				createTaskContent(context,orderId,packetId, filePath,orderReason,contentValue,contentType,isOffline);
				if(!isOffline){
					updateUploadStatus(context, filePath);
					ThreadPollsManager.getInstance().executeThread(new FileWriteThread(context,orderId,packetId,filePath,taskType,progressAction,account,version,fileType));
				}
			}else if(fileUploadMode==1 || fileUploadMode == 3){
				createTaskContent(context,orderId,packetId, filePath,orderReason,contentValue,contentType,isOffline);
				if(!isOffline){
					updateUploadStatus(context, filePath);
					ThreadPollsManager.getInstance().executeThread(new HttpFileUploadThread(context,orderId,packetId,filePath,taskType,progressAction,account,fileType));
				}
			}
		}else{
			WorkOrderIUDS iuds=new WorkOrderIUDS(context);
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(-2);
			taskContent.setContentName(filePath);
			iuds.updateUploadStatus(taskContent);
			Log.i(Tag, "文件不存在");
		}
	}

	public void uploadFileByHttp(Context context,String orderId,String packetId, String filePath,String orderReason,String contentValue,String taskType,String progressAction,String account,int contentType,String fileType){
		if(new File(filePath).exists()){
			createTaskContent(context,orderId,packetId, filePath,orderReason,contentValue,contentType,false);
			updateUploadStatus(context, filePath);
			ThreadPollsManager.getInstance().executeThread(new HttpFileUploadThread(context,orderId,packetId,filePath,taskType,progressAction,account,fileType));
		}else{
			WorkOrderIUDS iuds=new WorkOrderIUDS(context);
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(-2);
			taskContent.setContentName(filePath);
			iuds.updateUploadStatus(taskContent);
			Log.i(Tag, "文件不存在");
		}
	}
	
	@Override
	public synchronized void reUploadFile(Context context,String taskType,String progressAction,String account,String version,String fileType) {
		// TODO Auto-generated method stub
		if(!isReuploading){
			isReuploading = true;
			List<TableTaskContent> taskContents = new ArrayList<TableTaskContent>();
	    	WorkOrderIUDS iuds = new WorkOrderIUDS(context);
	    	taskContents = iuds.getReUploadFileRecords();
	    	if(taskContents.size() > 0){
	    		for(TableTaskContent bean : taskContents){
	    			if(bean != null){
	    				UploadFileService.getInstance().uploadFile(context,bean.getOrderId(),bean.getPacketId(), bean.getContentName(),"","",taskType,progressAction,account,version,bean.getContentType(),fileType,false);
	    			}
	    		}
	    	}
	    	taskContents = null;
	    	iuds = null;
	    	isReuploading = false;
		}
	}
	
	public synchronized void reUploadUploading(Context context,String taskType,String progressAction,String account,String version,String fileType) {
		// TODO Auto-generated method stub
		if(!isReuploading){
			isReuploading = true;
			List<TableTaskContent> taskContents = new ArrayList<TableTaskContent>();
	    	WorkOrderIUDS iuds = new WorkOrderIUDS(context);
	    	taskContents = iuds.getReUploadUploading();
	    	if(taskContents.size() > 0){
	    		for(TableTaskContent bean : taskContents){
	    			if(bean != null){
	    				UploadFileService.getInstance().uploadFile(context,bean.getOrderId(),bean.getPacketId(), bean.getContentName(),"","",taskType,progressAction,account,version,bean.getContentType(),fileType,false);
	    			}
	    		}
	    	}
	    	taskContents = null;
	    	iuds = null;
	    	isReuploading = false;
		}
	}
	
	//create tastContent
	private void createTaskContent(Context context,String orderId,String packetId, String filePath,String orderReason,String contentValue,int contentType,boolean isOffline){
		WorkOrderIUDS iuds=new WorkOrderIUDS(context);
		if(!iuds.isFileExists(filePath)){
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setPacketId(packetId);
			taskContent.setContentName(filePath);
			taskContent.setContentType(contentType);
			taskContent.setFileLength((int)new File(filePath).length());
			taskContent.setOrderReason(orderReason);
			taskContent.setContentValue(contentValue);
			taskContent.setStartPosition(0);
			taskContent.setMark(0);
			taskContent.setUploadStatus(isOffline?-1:0);
			taskContent.setOrderId(orderId);
			taskContent.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			taskContent.setSORT(iuds.getPhotoCountByOrderId(orderId)+1);
			taskContent.setUPDATE_TIME(System.currentTimeMillis());
			iuds.createTaskContent(taskContent);
			Log.i(Tag, "插入媒体文件记录");
			taskContent = null;
		}
		iuds = null;
	}
	
	//update UploadStatus
	public void updateUploadStatus(Context context, String filePath){
		//修改数据库上传状态为正在上传
		if(ThreadPollsManager.blockingQueue.size() < ThreadPollsManager.QueueMaxSize){
			WorkOrderIUDS iuds=new WorkOrderIUDS(context);
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(1);
			taskContent.setContentName(filePath);
			iuds.updateUploadStatus(taskContent);
			int fileUploadMode = fileUploadMode(context);
			iuds.updateMarkResultByFileName((fileUploadMode==0 || fileUploadMode == 2)?1:2, filePath);
			Log.i(Tag, "修改文件上传状态为正在上传");
			iuds = null;
			taskContent = null;
		}
	}
	
}
