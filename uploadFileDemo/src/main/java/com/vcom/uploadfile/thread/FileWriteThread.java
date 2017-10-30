package com.vcom.uploadfile.thread;
/****
 * �ֿ��ϴ��ļ�    �Ա�֤�ϵ�����
 * ÿ���ϴ����ļ���Ϊn����   
 * ÿ��������Ϊ128����  ÿ��Ϊ1k����ÿ�����128k��
 * ÿ���ϴ�һ������1k������
 * ��ʼλ��startpositionΪû�����ļ�����ʼλ�ã����ڼ�k��
 * 
 * ע�⣺ÿһ�������guidֵ��һ����
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
	private int limitTime = 25;//����1k����ӳ�ʱ�� ���Ͷ�����
//	public static final String ipAddress = "220.162.239.101";//ͨѶ��������ַ192.168.7.161
//	public static final String ipAddress = "220.162.239.101";//ͨѶ��������ַ192.168.7.161
//	public static final int filePort = 17777;//�����ķ����ļ��Ķ˿ں�
	private String taskType;//�������д���ļ���Ŀ¼��
	private String fileType = "jpg";
	public final int BlockLength = 128<<10;//�鳤��   128k   128*1024(B)
	private DatagramSocket socket;
	public final int SocketTimeOut = 5000;

	private Context context;
	
	private String orderId;
	private String packetId;
	private String filePath;
	private String MD5Name;//�ļ�MD5
	private long fileLength;//��λ�ֽ�
	
	private int blockCounts;//���ݿ���ܸ���
	private int startPosition = 0;//ÿһ�����ʼλ��
	private int lastBlockLength;//���һ��ĳ���
	private int lastBlockPacketges;//���һ��İ�����1k������
	private int lastPacketgeLength;//���1k�ĳ���
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
	
	//��ȡý��ͨѶip
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
	
	//̽�� udp ͨѶ
	@SuppressWarnings("resource")
	public boolean detectUdp(String mediaIp,int mediaPort){
		boolean isSuccess = false;// �Ƿ��ͳɹ�
		int resendTimes = 0;//ʧ���ط�����
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
						//�޸����ݿ�����λ��128*1024*(packetge-1)����ǰ�ϴ�״̬  ���Ա��������ش���

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
			Log.i(Tag, "̽��ʧ�ܷ�����:"+mediaIp+":"+mediaPort+"ͨѶ�쳣");
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
					//�޸����ݿ��ϴ�״̬Ϊδ�ϴ�״̬
					TableTaskContent taskContent = new TableTaskContent();
					taskContent.setUploadStatus(2);
					taskContent.setContentName(filePath);
					iuds.updateUploadStatus(taskContent);
					sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
					Log.i(Tag, "�ļ����ϴ���������ظ��ϴ�");
				}
				if(socket != null){
					socket.close();
				}
			}else{
				Log.i(Tag, "ThreadId��һ�£�ֹͣ�߳�");
				return;
			}
			iuds = null;
		}else{
			Log.i(Tag, "mediaIp:"+mediaIp+"mediaPort:"+mediaPort+"̽��ʧ��ֹͣ�ϴ�");
			cleanAndResendFile();
		}
		Log.i(Tag, "�߳̽���");
    }
	
	private boolean socketConnect()throws SocketException,UnknownHostException{
		if(!"".equals(mediaIp) && mediaPort >= 0){
			socket = new DatagramSocket();
			InetAddress serverAddress = InetAddress.getByName(mediaIp);
			socket.connect(serverAddress, mediaPort);
			return true;
		}else{
			//�޸����ݿ��ϴ�״̬Ϊδ�ϴ�״̬
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
					Log.i(Tag, "-----------Socket���ӳɹ�-------------");
				}else{
					//�޸����ݿ��ϴ�״̬Ϊδ�ϴ�״̬
					TableTaskContent taskContent = new TableTaskContent();
					taskContent.setUploadStatus(0);
					taskContent.setContentName(filePath);
					WorkOrderIUDS iuds = new WorkOrderIUDS(context);
					iuds.updateUploadStatus(taskContent);
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
				Log.i("Tag", "socket����ʧ�ܽ����߳�");
				//�޸����ݿ��ϴ�״̬Ϊδ�ϴ�״̬
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
					//��ȡ�ļ�����
//					Log.i(Tag, "FileDataLen="+FileData.length);
					int block = startPosition >= fileLength?blockCounts:startPosition>>7;//��ǰ�����ϴ��ĵ�n����  ��0��ʼ����  (startPosition/128)
				    while(block < blockCounts){
				    	String Guid = UUID.randomUUID().toString().replaceAll("-", "");//ÿ��������guid��֤һ��
				    	blockResendController = new BlockResendController(Guid);
//				    	if(block != 0){
//					    	Log.i(Tag, "�ÿ����ݶ�Ӧ��guid="+Guid);
							byte[] data = fixedLengthFileData(block);
//							Log.i(Tag, "��ȡ�ļ���"+block+"������");
//							Log.i(Tag, "�ÿ����ݴ�С="+data.length);
							int packetges = block == blockCounts-1?lastBlockPacketges:128;
//							Log.i(Tag, "�ÿ鹲��"+packetges+"��");
//							Log.i(Tag, "�ÿ�����ݳ���="+data.length);
							for(int i=0;i<packetges;i++){
								if(!sendPacketgeData(block,Guid,i,
								   BytesDealUtil.subBytes(data, i*1024, (block == blockCounts-1 && i == lastBlockPacketges-1)?lastPacketgeLength:1024))){
									Log.i(Tag, "����1kʧ�ܽ����߳�");
									return;
								}
							}//end for
							//test
//						  Log.i(Tag, "��ǰ�߳���="+this.getName());
//				    	}
					  if(ThreadId.equals(wsi.getThreadIdByPacketId(packetId))){
						  wsi.updateUpdateTime(packetId, System.currentTimeMillis());
						  sendAskingRequest(block,Guid);
						  if(!blockResendController.isSendResult()){//�ÿ����ݷ��ͽ��
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
							Log.i(Tag, "ThreadId��һ�£�ֹͣ�߳�");
							return;
					   }
				   }//end while
				}else{
					cleanAndResendFile();
				}
		   }
	}
	
	//��������̽���
	public boolean sendDetectPackData(){
		boolean isSuccess = false;// �Ƿ��ͳɹ�
		int resendTimes = 0;//ʧ���ط�����
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
						//�޸����ݿ�����λ��128*1024*(packetge-1)����ǰ�ϴ�״̬  ���Ա��������ش���
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
	
	//�������ݰ���1k���ݣ�
	private boolean sendPacketgeData(int block,String Guid,int index,byte[] Data){
		boolean isSuccess = false;// �Ƿ��ͳɹ�
		int resendTimes = 0;//ʧ���ط�����
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
						//�޸����ݿ�����λ��128*1024*(packetge-1)����ǰ�ϴ�״̬  ���Ա��������ش���
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
	
	//���Ϳ�ѯ������
	private void sendAskingRequest(int block,String Guid){
		boolean unReceived = true;//�Ƿ�δ�յ���������Ӧ��־
		int resendTimes = 0;//����ѯ���������  ����3�ν����߳�
		//�ɹ�����һ������ ��ѯ�ʰ���ȷ���Ƿ�����
		byte[] askingData = getAskingData(block,Guid,taskType,fileType);
		DatagramPacket packet = new DatagramPacket(askingData, askingData.length);
		byte[] askingResponseData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(askingResponseData,askingResponseData.length);
		try {
			socket.close();//�ȹر�֮ǰ��socket
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
//				Log.i(Tag, "�ɹ�����һ��ѯ�ʰ�");
//				CodeTestUtil.testAskingData(context,Thread.currentThread().getName(),packet.getData());
				socket.receive(receivePacket);
				unReceived = false;
				String cmd = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(receivePacket.getData(), 0, 2));
				Log.i(Tag, "������Ӧcmd="+cmd);
				if(cmd.equals("0001")){//ѯ����Ӧ
					blockAskingResponseDeal(receivePacket.getData(),block,Guid);
				}else if(cmd.equals("0003")){//�ļ������ϴ���Ӧ
					fileCompleteReceiveDeal(socket,receivePacket.getData());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(unReceived){
					if(resendTimes >= 5){//����5��ѯ�ʰ���û��Ӧ���޸����ݿ�״̬��startPosition�������߳�
						//�޸����ݿ�����λ��128*1024*(packetge-1)����ǰ�ϴ�״̬  ���Ա��������ش���
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
	
	//���ݿ�ѯ����Ӧ����
	private void blockAskingResponseDeal(byte[] responseData,int block,String Guid){
		Log.i(Tag, "���յ����ݿ��ϴ���Ӧ");
//		CodeTestUtil.testAskingResponseData(context,Thread.currentThread().getName(),responseData);
		int subPosition = 0;//��ȡ����λ��
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
				Log.i(Tag, "��δ�������յ�����");
				blockResendController.setSendResult(false);
	            //���²�����ʧ�İ�
				if(!blockResendController.resendable(Guid)){
					Log.i(Tag, "�ÿ��ش����������޶�");
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
				Log.i(Tag, "�ɹ��ϴ�һ��");
				if(block == blockCounts-1){//���һ�� 
					//�����ش�
//					cleanAndResendFile();
//					blockResendController.setSendResult(false);
//					TableTaskContent taskContent = new TableTaskContent();
//					taskContent.setStartPosition((int)fileLength);
//					taskContent.setContentName(filePath);
//					taskContent.setUploadStatus(2);
//					WorkOrderIUDS iuds=new WorkOrderIUDS(context);
//					iuds.updateStartAndStatus(taskContent);
//					Log.i(Tag, "�ļ��ϴ��ɹ�");
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
			Log.i(Tag, "��Ӧ��guid�뵱ǰ��Ӧ��guid��ƥ��");
			sendAskingRequest(block, Guid);
		}
	}

	//�ط���ʧ�İ�
	private void resendMissingData(DatagramSocket socket,String missingPacketges,int block,String Guid){
		String[] packetgeStr = missingPacketges.split(",");
		if(packetgeStr.length > 1){
//			Log.i(Tag, "lastBlockLength="+lastBlockLength);
//			Log.i(Tag, "lastPacketgeLength="+lastPacketgeLength);
			byte[] blockData = fixedLengthFileData(block);
			for(int i = 1; i<packetgeStr.length; i++){
				int index = Integer.parseInt(packetgeStr[i]);
//				Log.i(Tag, "�����ÿ鶪ʧ�ĵ�"+Integer.parseInt(packetgeStr[i])+"��");
				if(index<<10 < blockData.length){
					if(!sendPacketgeData(block,Guid,index,
					         BytesDealUtil.subBytes(blockData, index*1024, (block == blockCounts-1 && index == lastBlockPacketges-1)?lastPacketgeLength:1024))){
							 Log.i(Tag, "����1kʧ��");
							 return;
					}
				}else{
					Log.i(Tag, "index�������ݳ���");
					return;
				}
//				Log.i(Tag, "�����ÿ鶪ʧ�ĵ�"+Integer.parseInt(packetgeStr[i])+"��");
//				Log.i(Tag, "lastBlockLength="+lastBlockLength);
//				Log.i(Tag, "lastPacketgeLength="+lastPacketgeLength);
			}
		}   
	}
	
	//�ļ�����������Ӧ����
	private void fileCompleteReceiveDeal(DatagramSocket socket,byte[] completeData){
		Log.i(Tag, "���յ��ļ������ϴ���Ӧ");
		String completeMD5 = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(completeData, 2, 16));
//		String guid = BytesDealUtil.bytesToHexString(BytesDealUtil.subBytes(completeData, 18, 16));
		//�޸����ݿ��ϴ�״̬
		if(completeMD5.equals(MD5Name)){
			Log.i(Tag, "md5ƥ��ɹ�");
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
			iuds.updateMarkResultByFileName(1, filePath);//1:��udp����ϴ���
			Log.i(Tag, "�ļ��ϴ��ɹ�");
			sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
			socket.close();
		}else{
			Log.i(Tag, "������Ӧ���ļ�������");
//			cleanAndResendFile();
		}
	}
	
    //���ļ�����λ�ö�ȡ�̶����ȵ�����  128k��
	private byte[] fixedLengthFileData(int block){
		if(block == blockCounts-1){//���һ��
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
			Log.i(Tag, "�ļ�������");
		}
		return blockData;
	}
	
	//get sendData ����ͨѶͷ��byte[]
	private byte[] getSendData(int block,String Guid,int i,byte[] data){
		byte[] sendData = new byte[54+data.length];
		int addedLength = 0;
		
		String cmd = "0002";//�����ļ�
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
		
		if(block == blockCounts-1 && i == lastBlockPacketges-1){//���1k
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
	
	//��ȡѯ�ʰ�
	private byte[] getAskingData(int block,String Guid,String taskType,String fileType){
		int dataLen = 67+version.getBytes().length+taskType.getBytes().length+fileType.getBytes().length+date.getBytes().length+mobile.getBytes().length;
		byte[] askingData = new byte[dataLen];
		int addedLength = 0;
		
		String cmd = "0001";//ѯ�ʰ�
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
	
	//ʹ��handler�����׳�������ʾ
	private void sendHandlerMessage(String fileName,int startPosition,int fileLength){
		Intent it = new Intent(PROGRESS_ACTION);
		it.putExtra("fileName", fileName);
		it.putExtra("startPosition", startPosition);
		it.putExtra("fileLength", fileLength);
		context.sendBroadcast(it);
		Log.i(Tag, "�׳�������ʾ");
	}

	/*
	 * ������Ҫ���������ļ��ϴ�ʱÿ�����ݵ��ش�����
	 * �����޶��Ĵ�����ֹͣ�ϴ�
	 */
	private final class BlockResendController {
		private static final String Tag = "BlockResendController";
		private boolean sendResult;//�ÿ������ϴ����
		private String guid;//û�����ݶ�Ӧ��guid
		private int times;//�ش�����
		private static final int LIMIT_TIMES = 6;//�ش���������
		
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
				Log.i(Tag, "�ÿ����ݶ�Ӧ��guid����");
				return false;
			}
		}
	}

	//�����ϴ��쳣ʱ�������� ��ͷ��һ��
	private void cleanAndResendFile(){
		Log.i("FileWriteThread", "cleanAndResendFile--�����쳣");
		WorkOrderIUDS iuds=new WorkOrderIUDS(context);
		if(iuds.getUploadCountByRes(filePath, mediaIp, mediaPort, 1) >= 3){
			iuds.updateIsDieByRes(mediaIp, mediaPort, 1, 1);
//			Intent it = new Intent("com.vcom.resource_msg_Warning");
//			it.putExtra("type", "1");
//			it.putExtra("mediaIp", mediaIp);
//			it.putExtra("mediaPort", ""+mediaPort);
//			context.sendBroadcast(it);
			Log.i(Tag, "��ֹ�����������"+mediaIp);
//			ThreadPollsManager.getInstance().shutDownRunningThread();
		}
		//�ر�ԭ�ȵ�socket
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
			Log.i(Tag, "�޸��ļ��ϴ�״̬Ϊ�����ϴ�");
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
	
	//�ϴ��ļ� ��Ӧ�� pathurl
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
