package com.vcom.uploadfile.thread;
/****
 * 分块上传文件    以保证断点续传
 * 每个上传的文件分为n个块   
 * 每个块最大分为128个包  每包为1k（即每块最大128k）
 * 每次上传一个包即1k服务器
 * 开始位置startposition为没块在文件中起始位置（即第几k）
 * 
 * 注意：每一个块里的guid值是一样的
 ****/
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import com.vcom.uploadfile.activity.UploadProgressReceiver;
import com.vcom.uploadfile.db.WorkOrderIUDS;
import com.vcom.uploadfile.domain.TableTaskContent;
import com.vcom.uploadfile.domain.TableUploadLog;
import com.vcom.uploadfile.domain.TableUploadUrl;
import com.vcom.uploadfile.enums.EnumContentType;
import com.vcom.uploadfile.interfaces.UploadFileService;
import com.vcom.uploadfile.manager.ThreadPollsManager;
import com.vcom.uploadfile.util.BytesDealUtil;
import com.vcom.uploadfile.util.CodeTestUtil;
import com.vcom.uploadfile.util.MD5Util;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class FileWriteThread extends Thread {

	private final String Tag = "FileWriteThread";
	private final String ThreadId = UUID.randomUUID().toString().replaceAll("-", "");
	private String mobile ="";
	private String version = "";
	public String PROGRESS_ACTION;
	private String mediaIp = "";
	private int mediaPort = -1;
	private String fileUrl = "";
	private int limitTime = 25;//发送1k后的延迟时间 降低丢包率
//	public static final String ipAddress = "220.162.239.101";//通讯服务器地址192.168.7.161
//	public static final String ipAddress = "220.162.239.101";//通讯服务器地址192.168.7.161
//	public static final int filePort = 17777;//监听的发送文件的端口号
	private String taskType;//服务器中存放文件的目录名
	private String fileType = "jpg";
	public final int BlockLength = 128<<10;//块长度   128k   128*1024(B)
	private DatagramSocket socket;
	public final int SocketTimeOut = 5000;

	private Context context;
	
	private String orderId;
	private String packetId;
	private String filePath;
	private String MD5Name;//文件MD5
	private long fileLength;//单位字节
	
	private int blockCounts;//数据块的总个数
	private int startPosition = 0;//每一块的起始位置
	private int lastBlockLength;//最后一块的长度
	private int lastBlockPacketges;//最后一块的包数即1k包个数
	private int lastPacketgeLength;//最后1k的长度
	private BlockResendController blockResendController;
    private String date = "";
	
    
	public FileWriteThread(Context cxt,String OrderId,String packetid, String path,String TaskType,String progressAction,String mb,String vs,String fType){
		context = cxt;
		orderId = OrderId;
		packetId = packetid;
		filePath = path;
		taskType = TaskType;
		fileType = fType;
		PROGRESS_ACTION = progressAction;
		mobile = mb;
		version = vs;
		initData();
	}
	
	//获取媒体通讯ip
	private boolean getMediaIpAndPort(){
		boolean result = false;
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		TableUploadLog uploadLog = iuds.getFileHistoryUploadLog(filePath, 1);
		if(!"".equals(uploadLog.getIP())){
			mediaIp = uploadLog.getIP();
			mediaPort = uploadLog.getPORT();
			startPosition = uploadLog.getSTART_POSITION();
			fileUrl = uploadLog.getFILE_URL();
			iuds.autoAddUrlUploadCount(mediaIp, mediaPort, 1);
		}else{
			TableUploadUrl uploadUrl = iuds.getUploadUrlByCountMin(1);
			mediaIp = uploadUrl.getIP();
			mediaPort = uploadUrl.getPORT();
			fileUrl = uploadUrl.getFILE_URL();
			iuds.autoAddUrlUploadCount(mediaIp, mediaPort, 1);
		}
		Log.i(Tag, "ip="+mediaIp+",port="+mediaPort);
		limitTime = iuds.isRegulate()?25:10;
		if(iuds.isInterdicted()){
			context.sendBroadcast(new Intent("com.vcom.resource_msg_Warning"));
		}
        Log.i("limitTime=", ""+limitTime);
        if(detectUdp(mediaIp,mediaPort)){
			result = true;
		}
        return result;
	}
	
	//探测 udp 通讯
	@SuppressWarnings("resource")
	public boolean detectUdp(String mediaIp,int mediaPort){
		boolean isSuccess = false;// 是否发送成功
		int resendTimes = 0;//失败重发次数
		byte[] cmdByte = BytesDealUtil.hexStringToBytes("0004");
		DatagramPacket packet = new DatagramPacket(cmdByte, cmdByte.length);
		byte[] detectResponseData = new byte[2];
		DatagramPacket receivePacket = new DatagramPacket(detectResponseData,detectResponseData.length);
		while(!isSuccess){
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress serverAddress = InetAddress.getByName(mediaIp);
				socket.connect(serverAddress, mediaPort);
				socket.setSoTimeout(5000);
				socket.send(packet);
				socket.receive(receivePacket);
				String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(receivePacket.getData(), 0, 2));
				Log.i("cmd=", cmd);
				if(cmd.equals("0004")){
					isSuccess = true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(!isSuccess){
					if(resendTimes >= 2){
						//修改数据库续传位置128*1024*(packetge-1)及当前上传状态  （以便整个包重传）

						break;
					}else{
						resendTimes++;
					}
				}
			}
		}
		cmdByte = null;
		if(!isSuccess){
			WorkOrderIUDS iuds = new WorkOrderIUDS(context);
			iuds.updateIsDieByRes(mediaIp,mediaPort, 1, 1);
			Log.i(Tag, "探测失败服务器:"+mediaIp+":"+mediaPort+"通讯异常");
		}
		return isSuccess;
	}
	
	private void initData(){
		File f = new File(filePath);
		MD5Name = MD5Util.getMD5(f);
		Log.i("fileWriteTread", "md5="+MD5Name);
		if(f.exists()){
			fileLength = f.length();
		}
		blockCounts = fileLength%BlockLength==0?(int)(fileLength/BlockLength):(int)(fileLength/BlockLength)+1;
		Log.i(Tag, "blockCounts="+blockCounts);
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
//		startPosition = iuds.getStartPositionByFileName(filePath);
		date = iuds.getCreateTimeByFileName(filePath);
		iuds.updateThreadIdByPacketId(packetId, ThreadId);
		String[] dates = date.split(" ");
		if(null != dates){
			date = dates[0];
		}else{
			date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		Log.i(Tag, "startPosition="+startPosition);
		lastBlockLength = (int)(fileLength - (blockCounts-1)*BlockLength);
		lastBlockPacketges = lastBlockLength%1024 == 0?lastBlockLength/1024:(lastBlockLength/1024)+1;
		lastPacketgeLength = lastBlockLength-(lastBlockPacketges-1)*1024;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		if(getMediaIpAndPort()){
			if(ThreadId.equals(iuds.getThreadIdByPacketId(packetId))){
				if(iuds.getStartPositionByFileName(filePath) < fileLength){
					connectAndSend();
				}else{
					//修改数据库上传状态为未上传状态
					TableTaskContent taskContent = new TableTaskContent();
					taskContent.setUploadStatus(2);
					taskContent.setContentName(filePath);
					iuds.updateUploadStatus(taskContent);
					sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
					Log.i(Tag, "文件已上传完成无需重复上传");
				}
				if(socket != null){
					socket.close();
				}
			}else{
				Log.i(Tag, "ThreadId不一致，停止线程");
				return;
			}
			iuds = null;
		}else{
			Log.i(Tag, "mediaIp:"+mediaIp+"mediaPort:"+mediaPort+"探测失败停止上传");
			cleanAndResendFile();
		}
		Log.i(Tag, "线程结束");
    }
	
	private boolean socketConnect()throws SocketException,UnknownHostException{
		if(!"".equals(mediaIp) && mediaPort >= 0){
			socket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName(mediaIp);
			socket.connect(serverAddress, mediaPort);
			return true;
		}else{
			//修改数据库上传状态为未上传状态
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(0);
			taskContent.setContentName(filePath);
			WorkOrderIUDS iuds = new WorkOrderIUDS(context);
			iuds.updateUploadStatus(taskContent);
			return false;
		}
		
	}
	
	private void connectAndSend(){
			try {
				if(socketConnect()){
					WorkOrderIUDS iuds = new WorkOrderIUDS(context);
					if(iuds.isUploadLogExists(filePath, mediaIp, mediaPort, 1)){
						iuds.autoAddLogUploadCount(filePath, mediaIp, mediaPort, 1);
					}else{
						TableUploadLog uploadLog = new TableUploadLog();
						uploadLog.setFILE_PATH(filePath);
						uploadLog.setIP(mediaIp);
						uploadLog.setPORT(mediaPort);
						uploadLog.setFILE_URL(fileUrl);
						uploadLog.setUPLOAD_TYPE(1);
						uploadLog.setSTART_POSITION(0);
						uploadLog.setUPLOAD_COUNT(1);
						uploadLog.setCREATE_TIME(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						iuds.createUploadLog(uploadLog);
					}
					Log.i(Tag, "-----------Socket连接成功-------------");
				}else{
					//修改数据库上传状态为未上传状态
					TableTaskContent taskContent = new TableTaskContent();
					taskContent.setUploadStatus(0);
					taskContent.setContentName(filePath);
					WorkOrderIUDS iuds = new WorkOrderIUDS(context);
					iuds.updateUploadStatus(taskContent);
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
				Log.i("Tag", "socket连接失败结束线程");
				//修改数据库上传状态为未上传状态
				TableTaskContent taskContent = new TableTaskContent();
				taskContent.setUploadStatus(0);
				taskContent.setContentName(filePath);
				WorkOrderIUDS iuds = new WorkOrderIUDS(context);
				iuds.updateUploadStatus(taskContent);
				return;
			}
			if(socket.isConnected()){
				if(sendDetectPackData()){
					 WorkOrderIUDS wsi=new WorkOrderIUDS(context);
					 wsi.updateFileRootUrlByFileName(getHttpUrl(date,mobile), filePath);
					//获取文件数据
//					Log.i(Tag, "FileDataLen="+FileData.length);
					int block = startPosition >= fileLength?blockCounts:startPosition>>7;//当前正在上传的第n个块  从0开始计起  (startPosition/128)
				    while(block < blockCounts){
				    	String Guid = UUID.randomUUID().toString().replaceAll("-", "");//每块数据中guid保证一致
				    	blockResendController = new BlockResendController(Guid);
//				    	if(block != 0){
//					    	Log.i(Tag, "该块数据对应的guid="+Guid);
							byte[] data = fixedLengthFileData(block);
//							Log.i(Tag, "获取文件第"+block+"块数据");
//							Log.i(Tag, "该块数据大小="+data.length);
							int packetges = block == blockCounts-1?lastBlockPacketges:128;
//							Log.i(Tag, "该块共有"+packetges+"包");
//							Log.i(Tag, "该块的数据长度="+data.length);
							for(int i=0;i<packetges;i++){
								if(!sendPacketgeData(block,Guid,i,
								   BytesDealUtil.subBytes(data, i*1024, (block == blockCounts-1 && i == lastBlockPacketges-1)?lastPacketgeLength:1024))){
									Log.i(Tag, "发送1k失败结束线程");
									return;
								}
							}//end for
							//test
//						  Log.i(Tag, "当前线程名="+this.getName());
//				    	}
					  if(ThreadId.equals(wsi.getThreadIdByPacketId(packetId))){
						  wsi.updateUpdateTime(packetId, System.currentTimeMillis());
						  sendAskingRequest(block,Guid);
						  if(!blockResendController.isSendResult()){//该块数据发送结果
							  TableTaskContent tc = new TableTaskContent();
							  tc.setStartPosition(startPosition);
							  tc.setUploadStatus(0);
							  tc.setContentName(filePath);
							  WorkOrderIUDS ws=new WorkOrderIUDS(context);
							  ws.updateStartAndStatus(tc);
							  socket.close();
							  cleanAndResendFile();
							  return;
						  }
						  block++;
					  }else{
							Log.i(Tag, "ThreadId不一致，停止线程");
							return;
					   }
				   }//end while
				}else{
					cleanAndResendFile();
				}
		   }
	}
	
	//发送网络探测包
	public boolean sendDetectPackData(){
		boolean isSuccess = false;// 是否发送成功
		int resendTimes = 0;//失败重发次数
		byte[] cmdByte = BytesDealUtil.hexStringToBytes("0004");
		DatagramPacket packet = new DatagramPacket(cmdByte, cmdByte.length);
		byte[] detectResponseData = new byte[2];
		DatagramPacket receivePacket = new DatagramPacket(detectResponseData,detectResponseData.length);
		while(!isSuccess){
			try {
				socket.setSoTimeout(SocketTimeOut);
				socket.send(packet);
				socket.receive(receivePacket);
				String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(receivePacket.getData(), 0, 2));
				Log.i("cmd=", cmd);
				if(cmd.equals("0004")){
					isSuccess = true;
				}
//				try {
//					Thread.sleep(25);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(!isSuccess){
					if(resendTimes >= 2){
						//修改数据库续传位置128*1024*(packetge-1)及当前上传状态  （以便整个包重传）
						if(startPosition < fileLength){
							TableTaskContent taskContent = new TableTaskContent();
							taskContent.setStartPosition(startPosition);
							taskContent.setUploadStatus(0);
							taskContent.setContentName(filePath);
							WorkOrderIUDS iuds=new WorkOrderIUDS(context);
							iuds.updateStartAndStatus(taskContent);
						}
						socket.close();
						return isSuccess;
					}else{
						resendTimes++;
					}
				}
			}
		}
		cmdByte = null;
		return isSuccess;
	}
	
	//发送数据包（1k数据）
	private boolean sendPacketgeData(int block,String Guid,int index,byte[] Data){
		boolean isSuccess = false;// 是否发送成功
		int resendTimes = 0;//失败重发次数
		byte[] sendData = getSendData(block,Guid,index,Data);
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length);
		while(!isSuccess){
			try {
				socket.send(packet);
//				CodeTestUtil.testSendData(context,Thread.currentThread().getName(),sendData,Data);
				isSuccess = true;
				try {
					Thread.sleep(limitTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(!isSuccess){
					if(resendTimes >= 2){
						//修改数据库续传位置128*1024*(packetge-1)及当前上传状态  （以便整个包重传）
						if(startPosition < fileLength){
							TableTaskContent taskContent = new TableTaskContent();
							taskContent.setStartPosition(startPosition);
							taskContent.setUploadStatus(0);
							taskContent.setContentName(filePath);
							WorkOrderIUDS iuds=new WorkOrderIUDS(context);
							iuds.updateStartAndStatus(taskContent);
						}
						socket.close();
						return isSuccess;
					}else{
						resendTimes++;
					}
				}
			}
		}
		sendData = null;
		return isSuccess;
	}
	
	//发送块询问请求
	private void sendAskingRequest(int block,String Guid){
		boolean unReceived = true;//是否未收到服务器响应标志
		int resendTimes = 0;//发送询问请求次数  超过3次结束线程
		//成功发送一个包后 发询问包以确定是否续传
		byte[] askingData = getAskingData(block,Guid,taskType,fileType);
		DatagramPacket packet = new DatagramPacket(askingData, askingData.length);
		byte[] askingResponseData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(askingResponseData,askingResponseData.length);
		try {
			socket.close();//先关闭之前的socket
			socketConnect();
			socket.setSoTimeout(SocketTimeOut);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(unReceived){
			try {
				socket.send(packet);
//				Log.i(Tag, "成功发送一个询问包");
//				CodeTestUtil.testAskingData(context,Thread.currentThread().getName(),packet.getData());
				socket.receive(receivePacket);
				unReceived = false;
				String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(receivePacket.getData(), 0, 2));
				Log.i(Tag, "接收响应cmd="+cmd);
				if(cmd.equals("0001")){//询问响应
					blockAskingResponseDeal(receivePacket.getData(),block,Guid);
				}else if(cmd.equals("0003")){//文件完整上传响应
					fileCompleteReceiveDeal(socket,receivePacket.getData());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(unReceived){
					if(resendTimes >= 5){//发送5次询问包都没响应则修改数据库状态和startPosition并结束线程
						//修改数据库续传位置128*1024*(packetge-1)及当前上传状态  （以便整个包重传）
						TableTaskContent taskContent = new TableTaskContent();
						taskContent.setStartPosition(startPosition);
						taskContent.setUploadStatus(0);
						taskContent.setContentName(filePath);
						WorkOrderIUDS iuds=new WorkOrderIUDS(context);
						iuds.updateStartAndStatus(taskContent);
						socket.close();
//						cleanAndResendFile();
						return;
					}else{
						resendTimes++;
					}
				}
			}
		}
		askingData = null;
		askingResponseData = null;
	}
	
	//数据块询问响应处理
	private void blockAskingResponseDeal(byte[] responseData,int block,String Guid){
		Log.i(Tag, "接收到数据块上传响应");
//		CodeTestUtil.testAskingResponseData(context,Thread.currentThread().getName(),responseData);
		int subPosition = 0;//截取到的位置
		String responseCmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(responseData, subPosition, 2));
		subPosition += 2;
		String responseMd5 = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(responseData, subPosition, 16));
		subPosition += 16;
		int responseFileLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(responseData, subPosition, 4));
		subPosition += 4;
		String responseGuid = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(responseData, subPosition, 16));
		subPosition += 16;
		int responseStartPosition = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(responseData, subPosition, 4));
		subPosition += 4;
		int responseContentLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(responseData, subPosition, 4));
		subPosition += 4;
		String responseContent = new String(BytesDealUtil.subBytes(responseData, subPosition, responseContentLen));
		subPosition += responseContentLen;
		int responseTaskTypeLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(responseData, subPosition, 4));
		subPosition += 4;
		String responseTaskType = new String(BytesDealUtil.subBytes(responseData, subPosition, responseTaskTypeLen));
		subPosition += responseTaskTypeLen;
		int responseFileTypeLen = BytesDealUtil.byteToInt(BytesDealUtil.subBytes(responseData, subPosition, 4));
		subPosition += 4;
		String responseFileType = new String(BytesDealUtil.subBytes(responseData, subPosition, responseFileTypeLen));
		Log.i("blockResponse=", "cmd="+responseCmd+"responseMd5="+responseMd5+"responseFileLen"+responseFileLen+"responseGuid"+responseGuid+"responseStartPosition"+responseStartPosition+"responseContentLen"+responseContentLen);
		if(Guid.equals(responseGuid)){
			if(responseContentLen > 0){
				Log.i(Tag, "有未完整接收的数据");
				blockResendController.setSendResult(false);
	            //重新补发丢失的包
				if(!blockResendController.resendable(Guid)){
					Log.i(Tag, "该块重传次数超出限定");
//					cleanAndResendFile();
//					TableTaskContent taskContent = new TableTaskContent();
//					taskContent.setStartPosition(startPosition);
//					taskContent.setUploadStatus(0);
//					taskContent.setContentName(filePath);
//					WorkOrderIUDS iuds=new WorkOrderIUDS(context);
//					iuds.updateStartAndStatus(taskContent);
					return;
				}else{
					resendMissingData(socket,responseContent,block,Guid);
					sendAskingRequest(block,Guid);
				}
			}else{
				Log.i(Tag, "成功上传一块");
				if(block == blockCounts-1){//最后一块 
					//清零重传
//					cleanAndResendFile();
//					blockResendController.setSendResult(false);
//					TableTaskContent taskContent = new TableTaskContent();
//					taskContent.setStartPosition((int)fileLength);
//					taskContent.setContentName(filePath);
//					taskContent.setUploadStatus(2);
//					WorkOrderIUDS iuds=new WorkOrderIUDS(context);
//					iuds.updateStartAndStatus(taskContent);
//					Log.i(Tag, "文件上传成功");
//					sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
					socket.close();
				}else{
					startPosition += 128;
					blockResendController.setSendResult(true);
					TableTaskContent taskContent = new TableTaskContent();
					taskContent.setStartPosition(startPosition);
					taskContent.setContentName(filePath);
					WorkOrderIUDS iuds=new WorkOrderIUDS(context);
					iuds.updateStartPosition(taskContent);
					iuds.updateLogStartPosition(filePath, mediaIp, mediaPort, 1, startPosition);
					sendHandlerMessage(filePath,startPosition,(int)fileLength);
				}
		     }	
		}else{
			Log.i(Tag, "响应的guid与当前对应的guid不匹配");
			sendAskingRequest(block, Guid);
		}
	}

	//重发丢失的包
	private void resendMissingData(DatagramSocket socket,String missingPacketges,int block,String Guid){
		String[] packetgeStr = missingPacketges.split(",");
		if(packetgeStr.length > 1){
//			Log.i(Tag, "lastBlockLength="+lastBlockLength);
//			Log.i(Tag, "lastPacketgeLength="+lastPacketgeLength);
			byte[] blockData = fixedLengthFileData(block);
			for(int i = 1; i<packetgeStr.length; i++){
				int index = Integer.parseInt(packetgeStr[i]);
//				Log.i(Tag, "补发该块丢失的第"+Integer.parseInt(packetgeStr[i])+"包");
				if(index<<10 < blockData.length){
					if(!sendPacketgeData(block,Guid,index,
					         BytesDealUtil.subBytes(blockData, index*1024, (block == blockCounts-1 && index == lastBlockPacketges-1)?lastPacketgeLength:1024))){
							 Log.i(Tag, "发送1k失败");
							 return;
					}
				}else{
					Log.i(Tag, "index超出数据长度");
					return;
				}
//				Log.i(Tag, "补发该块丢失的第"+Integer.parseInt(packetgeStr[i])+"包");
//				Log.i(Tag, "lastBlockLength="+lastBlockLength);
//				Log.i(Tag, "lastPacketgeLength="+lastPacketgeLength);
			}
		}   
	}
	
	//文件完整接收响应处理
	private void fileCompleteReceiveDeal(DatagramSocket socket,byte[] completeData){
		Log.i(Tag, "接收到文件完整上传响应");
		String completeMD5 = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(completeData, 2, 16));
//		String guid = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(completeData, 18, 16));
		//修改数据库上传状态
		if(completeMD5.equals(MD5Name)){
			Log.i(Tag, "md5匹配成功");
//			CodeTestUtil.testFileComplete(context,Thread.currentThread().getName(), MD5Name);
			blockResendController.setSendResult(true);
			startPosition = (int)fileLength;
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setStartPosition((int)fileLength);
			taskContent.setUploadStatus(2);
			taskContent.setContentName(filePath);
			WorkOrderIUDS iuds=new WorkOrderIUDS(context);
			iuds.updateStartAndStatus(taskContent);
			iuds.updateLogStartPosition(filePath, mediaIp, mediaPort, 1, (int)fileLength);
			iuds.updateMarkResultByFileName(1, filePath);//1:以udp完成上传的
			Log.i(Tag, "文件上传成功");
			sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
			socket.close();
		}else{
			Log.i(Tag, "接收响应的文件名有误");
//			cleanAndResendFile();
		}
	}
	
    //从文件具体位置读取固定长度的数据  128k包
	private byte[] fixedLengthFileData(int block){
		if(block == blockCounts-1){//最后一块
			return getBlockData(block, block*BlockLength, lastBlockLength);
		}else{
			return getBlockData(block, block*BlockLength, BlockLength);
		}
	}
	
	//get blockData
	private byte[] getBlockData(int block,int skipLen,int blockLen){
		File f =new File(filePath);
		byte[] blockData = new byte[blockLen];
		if(f.exists()){
				try {
					FileInputStream is = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(is);
					bis.skip(skipLen);
					bis.read(blockData, 0, blockLen);
					is.close();
					bis.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}else{
			Log.i(Tag, "文件不存在");
		}
		return blockData;
	}
	
	//get sendData 加上通讯头部byte[]
	private byte[] getSendData(int block,String Guid,int i,byte[] data){
		byte[] sendData = new byte[54+data.length];
		int addedLength = 0;
		
		String cmd = "0002";//发送文件
		byte[] cmdByte = BytesDealUtil.hexStringToBytes(cmd);
		System.arraycopy(cmdByte, 0, sendData, addedLength, cmdByte.length);
		addedLength += cmdByte.length;
	
		String md5FileName = MD5Name;
		byte[] md5FileNameByte = BytesDealUtil.hexStringToBytes(md5FileName);
		System.arraycopy(md5FileNameByte, 0, sendData, addedLength, md5FileNameByte.length);
		addedLength += md5FileNameByte.length;
		
		byte[] fileLengthByte = BytesDealUtil.intToByte((int)fileLength);
		System.arraycopy(fileLengthByte, 0, sendData, addedLength, fileLengthByte.length);
		addedLength += fileLengthByte.length;
		
		String guid = Guid;
		byte[] guidByte = BytesDealUtil.hexStringToBytes(guid);
		System.arraycopy(guidByte, 0, sendData, addedLength, guidByte.length);
		addedLength += guidByte.length;
		
		byte[] startByte = BytesDealUtil.intToByte(startPosition<<10);
		System.arraycopy(startByte, 0, sendData, addedLength, startByte.length);
		addedLength += startByte.length;
		
		if(block == blockCounts-1){
			int packets_1k = lastBlockPacketges;
			byte[] packets_1kByte = BytesDealUtil.intToByte(packets_1k);
			System.arraycopy(packets_1kByte, 0, sendData, addedLength, packets_1kByte.length);
			addedLength += packets_1kByte.length;
		}else{
			int packets_1k = 128;
			byte[] packets_1kByte = BytesDealUtil.intToByte(packets_1k);
			System.arraycopy(packets_1kByte, 0, sendData, addedLength, packets_1kByte.length);
			addedLength += packets_1kByte.length;
		}
		
		int index = i;
		byte[] indexByte = BytesDealUtil.intToByte(index);
		System.arraycopy(indexByte, 0, sendData, addedLength, indexByte.length);
		addedLength += indexByte.length;
		
		if(block == blockCounts-1 && i == lastBlockPacketges-1){//最后1k
			int len = lastPacketgeLength;
			byte[] lenByte = BytesDealUtil.intToByte(len);
			System.arraycopy(lenByte, 0, sendData, addedLength, lenByte.length);
			addedLength += lenByte.length;
		}else{
			int len = 1024;
			byte[] lenByte = BytesDealUtil.intToByte(len);
			System.arraycopy(lenByte, 0, sendData, addedLength, lenByte.length);
			addedLength += lenByte.length;
		}
		System.arraycopy(data, 0, sendData, addedLength, data.length);
		return sendData;
	}
	
	//获取询问包
	private byte[] getAskingData(int block,String Guid,String taskType,String fileType){
		int dataLen = 67+version.getBytes().length+taskType.getBytes().length+fileType.getBytes().length+date.getBytes().length+mobile.getBytes().length;
		byte[] askingData = new byte[dataLen];
		int addedLength = 0;
		
		String cmd = "0001";//询问包
		byte[] cmdByte = BytesDealUtil.hexStringToBytes(cmd);
		System.arraycopy(cmdByte, 0, askingData, addedLength, cmdByte.length);
		addedLength += cmdByte.length;
		
		int versionLen = version.getBytes().length;
		byte[] versionLenByte = BytesDealUtil.intToByte(versionLen);
		System.arraycopy(versionLenByte, 0, askingData, addedLength, versionLenByte.length);
		addedLength += versionLenByte.length;
		
		byte[] versionByte = version.getBytes();
		System.arraycopy(versionByte, 0, askingData, addedLength, versionByte.length);
		addedLength += versionByte.length;
		
		String md5FileName = MD5Name;
		byte[] md5FileNameByte = BytesDealUtil.hexStringToBytes(md5FileName);
		System.arraycopy(md5FileNameByte, 0, askingData, addedLength, md5FileNameByte.length);
		addedLength += md5FileNameByte.length;
		
		byte[] fileLengthByte = BytesDealUtil.intToByte((int)fileLength);
		System.arraycopy(fileLengthByte, 0, askingData, addedLength, fileLengthByte.length);
		addedLength += fileLengthByte.length;
		
		String guid = Guid;
		byte[] guidByte = BytesDealUtil.hexStringToBytes(guid);
		System.arraycopy(guidByte, 0, askingData, addedLength, guidByte.length);
		addedLength += guidByte.length;
		
		int start = startPosition<<10;
		byte[] startByte = BytesDealUtil.intToByte(start);
		System.arraycopy(startByte, 0, askingData, addedLength, startByte.length);
		addedLength += startByte.length;
		
		if(block == blockCounts-1){
			int packets_1k = lastBlockPacketges;
			byte[] packets_1kByte = BytesDealUtil.intToByte(packets_1k);
			System.arraycopy(packets_1kByte, 0, askingData, addedLength, packets_1kByte.length);
			addedLength += packets_1kByte.length;
		}else{
			int packets_1k = 128;
			byte[] packets_1kByte = BytesDealUtil.intToByte(packets_1k);
			System.arraycopy(packets_1kByte, 0, askingData, addedLength, packets_1kByte.length);
			addedLength += packets_1kByte.length;
		}
		
		int taskTypeLen = taskType.getBytes().length;
		byte[] taskTypeLenByte = BytesDealUtil.intToByte(taskTypeLen);
		System.arraycopy(taskTypeLenByte, 0, askingData, addedLength, taskTypeLenByte.length);
		addedLength += taskTypeLenByte.length;
		
		byte[] taskTypeByte = taskType.getBytes();
		System.arraycopy(taskTypeByte, 0, askingData, addedLength, taskTypeByte.length);
		addedLength += taskTypeByte.length;
		
		int fileTypeLen = fileType.getBytes().length;
		byte[] fileTypeLenByte = BytesDealUtil.intToByte(fileTypeLen);
		System.arraycopy(fileTypeLenByte, 0, askingData, addedLength, fileTypeLenByte.length);
		addedLength += fileTypeLenByte.length;
		
		byte[] fileTypeByte = fileType.getBytes();
		System.arraycopy(fileTypeByte, 0, askingData, addedLength, fileTypeByte.length);
		addedLength += fileTypeByte.length;
		
		int dateLen = date.getBytes().length;
		byte[] dateLenByte = BytesDealUtil.intToByte(dateLen);
		System.arraycopy(dateLenByte, 0, askingData, addedLength, dateLenByte.length);
		addedLength += dateLenByte.length;
		
		byte[] dateByte = date.getBytes();
		System.arraycopy(dateByte, 0, askingData, addedLength, dateByte.length);
		addedLength += dateByte.length;
		
		int mobileLen = mobile.getBytes().length;
		byte[] mobileLenByte = BytesDealUtil.intToByte(mobileLen);
		System.arraycopy(mobileLenByte, 0, askingData, addedLength, mobileLenByte.length);
		addedLength += mobileLenByte.length;
		
		byte[] mobileByte = mobile.getBytes();
		System.arraycopy(mobileByte, 0, askingData, addedLength, mobileByte.length);
		addedLength += mobileByte.length;
		
		byte[] deviceByte =  BytesDealUtil.hexStringToBytes("01");
		System.arraycopy(deviceByte, 0, askingData, addedLength, deviceByte.length);
		return askingData;
	}
	
	//使用handler机制抛出进度显示
	private void sendHandlerMessage(String fileName,int startPosition,int fileLength){
		Intent it = new Intent(PROGRESS_ACTION);
		it.putExtra("fileName", fileName);
		it.putExtra("startPosition", startPosition);
		it.putExtra("fileLength", fileLength);
		context.sendBroadcast(it);
		Log.i(Tag, "抛出进度显示");
	}

	/*
	 * 该类主要用来控制文件上传时每块数据的重传次数
	 * 超过限定的次数则停止上传
	 */
	private final class BlockResendController {
		private static final String Tag = "BlockResendController";
		private boolean sendResult;//该块数据上传结果
		private String guid;//没块数据对应的guid
		private int times;//重传次数
		private static final int LIMIT_TIMES = 6;//重传次数限制
		
		public BlockResendController(String Guid){
			this.guid = Guid;
			times = 1;
			sendResult = false;
		}
		
		public boolean isSendResult() {
			Log.i(Tag, "sendResult="+sendResult);
			return sendResult;
		}

		public void setSendResult(boolean sendResult) {
			this.sendResult = sendResult;
		}

		public boolean resendable(String Guid){
			if(Guid.equals(guid)){
				times++;
				return times >= LIMIT_TIMES?false:true;
			}else{
				Log.i(Tag, "该块数据对应的guid有误");
				return false;
			}
		}
	}

	//出现上传异常时数据清零 重头发一次
	private void cleanAndResendFile(){
		Log.i("FileWriteThread", "cleanAndResendFile--传输异常");
		WorkOrderIUDS iuds=new WorkOrderIUDS(context);
		if(iuds.getUploadCountByRes(filePath, mediaIp, mediaPort, 1) >= 3){
			iuds.updateIsDieByRes(mediaIp, mediaPort, 1, 1);
//			Intent it = new Intent("com.vcom.resource_msg_Warning");
//			it.putExtra("type", "1");
//			it.putExtra("mediaIp", mediaIp);
//			it.putExtra("mediaPort", ""+mediaPort);
//			context.sendBroadcast(it);
			Log.i(Tag, "禁止分配服务器："+mediaIp);
//			ThreadPollsManager.getInstance().shutDownRunningThread();
		}
		//关闭原先的socket
		if(null != socket){
			socket.close();
		}
		SharedPreferences sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		int uploadMode = Integer.parseInt(sp.getString("FILEUPLOADMODE", "0"));
		if(uploadMode == 0){
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(1);
			taskContent.setContentName(filePath);
			iuds.updateUploadStatus(taskContent);
			iuds.updateMarkResultByFileName(2, filePath);
			Log.i(Tag, "修改文件上传状态为正在上传");
			ThreadPollsManager.getInstance().executeThread(new HttpFileUploadThread(context,orderId,packetId,filePath,taskType,PROGRESS_ACTION,mobile,fileType));
			return;
		}else{
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(0);
			taskContent.setContentName(filePath);
			iuds.updateUploadStatus(taskContent);
			return;
		}
	}
	
	//上传文件 对应的 pathurl
	public String getHttpUrl(String date,String mobile){
		if(date == null || date.equals("")){
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		String[] dateArray = date.split("-");
//		SharedPreferences sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		StringBuffer buffer = new StringBuffer();
		buffer.append("http://");
		buffer.append(fileUrl);
		buffer.append("/UploadFile/");
		buffer.append(taskType);
		buffer.append("/"+dateArray[0]+dateArray[1]+"/"+dateArray[2]+"/"+mobile+"/");
		buffer.append("UDP/");
		buffer.toString();
		return buffer.toString();
	}
	
}
