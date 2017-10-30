package com.vcom.uploadfile.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.vcom.uploadfile.db.WorkOrderIUDS;
import com.vcom.uploadfile.domain.TableTaskContent;
import com.vcom.uploadfile.domain.TableUploadLog;
import com.vcom.uploadfile.domain.TableUploadUrl;
import com.vcom.uploadfile.enums.EnumContentType;
import com.vcom.uploadfile.interfaces.UploadFileService;
import com.vcom.uploadfile.manager.ThreadPollsManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

public class HttpFileUploadThread extends Thread {

	private final String Tag = "HttpFileUploadThread";
	private final String ThreadId = UUID.randomUUID().toString().replaceAll("-", "");
	private String PROGRESS_ACTION;//进度显示广播 action
	private String FileUploadUrl = "";
	
	private Context context;
	private String taskType;//服务器中存放文件的目录名
	private String fileType = ".jpg";
	private String orderId;
	private String packetId;
	private String filePath;
	private String fileName;//文件名含后缀".jpg"
	private int startPosition;
	private String fileUrl = "";
	private int blocks;//文件块数
	private final int blockLength = 1024<<10;//每块最大长度
	private long fileLength;//单位字节
	private String date = "";
	private String mobile = "";
	public  int SocketTimeOut = 3000;
	public  int ReadTimeOut = 5000;
	
	public HttpFileUploadThread(Context cxt,String OrderId,String packetid, String path,String TaskType,String progressAction,String mb,String fType){
		context = cxt;
		orderId = OrderId;
		packetId = packetid;
		filePath = path;
		taskType = TaskType;
		fileType = "."+fType;
		mobile = mb;
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		date = iuds.getCreateTimeByFileName(filePath);
		String[] dates = date.split(" ");
		if(null != dates){
			date = dates[0];
		}else{
			date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		PROGRESS_ACTION = progressAction;
		initData();
	}
	
	private void initData(){
		SocketTimeOut = UploadFileService.getInstance().getUploadSocketTimeOut(context);
		ReadTimeOut = UploadFileService.getInstance().getUploadReadTimeOut(context);
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		iuds.updateThreadIdByPacketId(packetId, ThreadId);
		File f = new File(filePath);
		if(f.exists()){
			fileLength = f.length();
			fileName = f.getName();
		}
	}
	
	//获取Http文件上传url
	private boolean getHttpFileUploadUrl(){
		boolean result = false;
		WorkOrderIUDS iuds = new WorkOrderIUDS(context);
		TableUploadLog uploadLog = iuds.getFileHistoryUploadLog(filePath, 2);
		if(!"".equals(uploadLog.getIP())){
			FileUploadUrl = uploadLog.getIP();
			startPosition = uploadLog.getSTART_POSITION();
			fileUrl = uploadLog.getFILE_URL();
			iuds.autoAddUrlUploadCount(FileUploadUrl, 0, 2);
		}else{
			TableUploadUrl uploadUrl = iuds.getUploadUrlByCountMin(2);
			FileUploadUrl = uploadUrl.getIP();
			fileUrl = uploadUrl.getFILE_URL();
			iuds.autoAddUrlUploadCount(FileUploadUrl, 0, 2);
		}
		Log.i(Tag, "uploadUrl="+FileUploadUrl);
		if(iuds.isInterdicted()){
			context.sendBroadcast(new Intent("com.vcom.resource_msg_Warning"));
		}
		if(detectHttp(FileUploadUrl)){
			result = true;
		}
		return result;
	}
	
	//探测 http通讯
	public boolean detectHttp(String UrlAddress){
		boolean result = false;
		try {
			URL url = new URL(UrlAddress);
			URLConnection conn = url.openConnection();
			conn.connect();
			result = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		if(!result){
			WorkOrderIUDS iuds = new WorkOrderIUDS(context);
			iuds.updateIsDieByRes(UrlAddress,0, 2, 1);
			Log.i(Tag, "探测失败服务器:"+UrlAddress+"通讯异常");
		}
		return result;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		WorkOrderIUDS Iuds = new WorkOrderIUDS(context);
		if(getHttpFileUploadUrl()){
			if(ThreadId.equals(Iuds.getThreadIdByPacketId(packetId))){
				if(!"".equals(FileUploadUrl)){
					WorkOrderIUDS iuds = new WorkOrderIUDS(context);
					if(iuds.isUploadLogExists(filePath,FileUploadUrl, 0, 2)){
						iuds.autoAddLogUploadCount(filePath, FileUploadUrl, 0, 2);
					}else{
						TableUploadLog uploadLog = new TableUploadLog();
						uploadLog.setFILE_PATH(filePath);
						uploadLog.setIP(FileUploadUrl);
						uploadLog.setPORT(0);
						uploadLog.setFILE_URL(fileUrl);
						uploadLog.setUPLOAD_TYPE(2);
						uploadLog.setSTART_POSITION(0);
						uploadLog.setUPLOAD_COUNT(1);
						uploadLog.setCREATE_TIME(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						iuds.createUploadLog(uploadLog);
					}
					startPosition = Integer.parseInt(GetFilePosi(FileUploadUrl,packetId+".vcom",mobile,date,taskType));//单位字节
					if(startPosition == fileLength){
						iuds.updateMarkAndStatus((int)fileLength, 2, filePath);
						iuds.updateMarkResultByFileName(2, filePath);//2:以http完成上传的
						sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
						return;
					}else if(startPosition == -1){
						TableTaskContent taskContent = new TableTaskContent();
						taskContent.setUploadStatus(0);
						taskContent.setContentName(filePath);
						iuds.updateUploadStatus(taskContent);
						if(iuds.getUploadCountByRes(filePath, FileUploadUrl, 0, 2) >= 3){
							iuds.updateIsDieByRes(FileUploadUrl, 0, 2, 1);
//							Intent it = new Intent("com.vcom.resource_msg_Warning");
//							it.putExtra("type", "2");
//							it.putExtra("mediaIp", FileUploadUrl);
//							it.putExtra("mediaPort", "");
//							context.sendBroadcast(it);
							Log.i(Tag, "禁止分配服务器："+FileUploadUrl);
//							ThreadPollsManager.getInstance().shutDownRunningThread();
						}
						Log.i(Tag, "线程结束");
						return;
					}else{
						Log.i("startPosition=", ""+startPosition);
						blocks = (int)((fileLength-startPosition)%(1024<<10) == 0?(fileLength-startPosition)/(1024<<10):(fileLength-startPosition)/(1024<<10)+1);
						Log.i("blocks=", ""+blocks);
						sendFileByBlocks();
					}
				}else{
					WorkOrderIUDS iuds = new WorkOrderIUDS(context);
					TableTaskContent taskContent = new TableTaskContent();
					taskContent.setUploadStatus(0);
					taskContent.setContentName(filePath);
					iuds.updateUploadStatus(taskContent);
					Intent it = new Intent();
	                it.setAction("VCOM.UPLOADFILE.HTTP.RESULT");
	                it.putExtra("result", "-1");
	                it.putExtra("msg", "上传地址无效");
	                context.sendBroadcast(it);
					Log.i(Tag, "线程结束");
					return;
				}
			}else{
				Log.i(Tag, "ThreadId不一致，停止线程");
				return;
			}
			Iuds = null;
		}else{
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setUploadStatus(0);
			taskContent.setContentName(filePath);
			Iuds.updateUploadStatus(taskContent);
			Log.i(Tag, "FileUploadUrl="+FileUploadUrl+"探测失败停止上传");
		}
		Log.i(Tag, "线程结束");
	}
	
	//分段上传文件
	public void sendFileByBlocks(){
		TableTaskContent taskContent = new TableTaskContent();
		taskContent.setContentName(filePath);
		WorkOrderIUDS iuds=new WorkOrderIUDS(context);
		iuds.updateFileRootUrlByFileName(getHttpUrl(date,mobile), filePath);
		for(int block=0 ; block<blocks ; block++){
			Log.i(Tag, "准备上传第"+block+"块");
			String fileDataBaseStr = Base64.encodeToString(getBlockData(startPosition), Base64.DEFAULT);
			iuds.updateUpdateTime(packetId, System.currentTimeMillis());
			if(block != blocks-1){
				if(TransFile(fileDataBaseStr, packetId+".vcom", false, fileType, "admin", "1").contains("分段")){
					//修改数据库startPosition及上传状态
					startPosition += blockLength;
					taskContent.setMark(startPosition);
					iuds.updateMarkByFileName(startPosition, filePath);
					iuds.updateLogStartPosition(filePath, FileUploadUrl, 0, 2, startPosition);
					sendHandlerMessage(filePath,startPosition>>10,(int)fileLength);
					Log.i(Tag, "成功上传一块");
				}else{
					//异常情况下应该询问startPosition
					
					
					iuds.updateMarkAndStatus(startPosition, 0, filePath);
					Log.i(Tag, "上传该块异常");
					break;
				}
			}else{//最后一块
				String result = TransFile(fileDataBaseStr, packetId+".vcom", true, fileType, "admin", "1");
				if(result.contains("传输完成")){
					//修改数据库startPosition及上传状态
					iuds.updateMarkAndStatus((int)fileLength, 2, filePath);
					iuds.updateMarkResultByFileName(2, filePath);//2:以http完成上传的
					iuds.updateLogStartPosition(filePath, FileUploadUrl, 0, 2, (int)fileLength);
					sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
					Log.i(Tag, "文件上传成功");
				}else if(result.contains("文件已经存在")){
					iuds.updateMarkAndStatus((int)fileLength, 2, filePath);
					iuds.updateMarkResultByFileName(2, filePath);//2:以http完成上传的
					iuds.updateLogStartPosition(filePath, FileUploadUrl, 0, 2, (int)fileLength);
					sendHandlerMessage(filePath,(int)fileLength,(int)fileLength);
					Log.i(Tag, "文件已上传成功");
				}else{
					//异常情况下应该询问startPosition
					
					
					iuds.updateMarkAndStatus(startPosition, 0, filePath);
					Log.i(Tag, "上传该块异常");
					break;
				}
			}
			fileDataBaseStr = null;
		}
		taskContent = null;
		iuds = null;
	}

	//获取已上传文件位置
	public String GetFilePosi(String url,String fileName,String mobile,String date,String agent) {
		Log.i(Tag, "GetFilePosi");
		boolean isSuccess = false;
        int retryTimes = 1;
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        while(!isSuccess){
        	try {
                URL realUrl = new URL(url+"/GetFilePosi");
                // 打开和URL之间的连接
                URLConnection conn = realUrl.openConnection();
                conn.setConnectTimeout(SocketTimeOut);
                conn.setReadTimeout(ReadTimeOut);
                // 设置通用的请求属性
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                // 发送POST请求必须设置如下两行
                conn.setDoOutput(true);
                conn.setDoInput(true);
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                out.print("fileName="+fileName+"&mobile="+mobile+"&sysdate="+date+"&agent="+agent);
                // flush输出流的缓冲
                out.flush();
                out.close();
                // 定义BufferedReader输入流来读取URL的响应
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                Log.i("GetFilePosi", "startposition="+result);
                result = analyzeNormalResponse(result);
                if(result.equals("-1")){
                	result = "0";
                }
                isSuccess = true;
                in.close();
            } catch (Exception e) {
                System.out.println("发送 POST 请求出现异常！"+e);
                e.printStackTrace();
                isSuccess = false;
                result = "-1";
                Intent it = new Intent();
                it.setAction("VCOM.UPLOADFILE.HTTP.RESULT");
                it.putExtra("result", "-1");
                it.putExtra("msg", "网络传输超时");
                context.sendBroadcast(it);
            }
            //使用finally块来关闭输出流、输入流
            finally{
            	if(!isSuccess){
//	        		try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
	        		if(retryTimes >= 1){
						break;
					}else{
						retryTimes++;
					}
	        	}
            }
        }
        return result.equals("")?"-1":result;
    }    
	
	//传送文件
	public String TransFile(String fileBt, String fileName, boolean ifEnd,
			String extension, String name, String pwd) {
		// TODO Auto-generated method stub
		Log.i(Tag, "TransFile");
		StringBuilder result = new StringBuilder();
		boolean isSuccess = false;
		int retryTimes = 1;
		while(!isSuccess){
			try {  
	            String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
	            		        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
	            		           "<soap:Body>" +
	            		               "<TransFile xmlns=\"http://tempuri.org/\">" +
	            		                   "<fileBt>"+fileBt+"</fileBt>" +
	            		                   "<fileName>"+fileName+"</fileName>" +
	            		                   "<ifEnd>"+ifEnd+"</ifEnd>" +
	            		                   "<extension>"+extension+"</extension>" +
	            		                   "<name>"+name+"</name>" +
	            		                   "<pwd>"+pwd+"</pwd>" +
	            		                   "<mobile>"+mobile+"</mobile>" +
	            		                   "<sysdate>"+(date.equals("")?new SimpleDateFormat("yyyy-MM-dd").format(new Date()):date)+"</sysdate>" +
	            		                   "<agent>"+taskType+"</agent>" +
	            		               "</TransFile>" +
	            		           "</soap:Body>" +
	            		        "</soap:Envelope>";  
	            URL url = new URL(FileUploadUrl+"?wsdl");
	            URLConnection conn = url.openConnection(); 
	            conn.setConnectTimeout(SocketTimeOut);
                conn.setReadTimeout(ReadTimeOut);
	            conn.setUseCaches(false);  
	            conn.setDoInput(true);  
	            conn.setDoOutput(true);  
	  
	            conn.setRequestProperty("Content-Length", Integer.toString(soap.length()));  
	            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");  
	            conn.setRequestProperty("SOAPAction", "http://tempuri.org/TransFile");  
	  
	            OutputStream os = conn.getOutputStream();  
	            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");  
	            osw.write(soap);  
	            osw.flush();  
	            osw.close();  
	            Log.i(Tag, "writedata--success,readtimeout="+conn.getReadTimeout());
	            
	            String sCurrentLine = null; 
	            InputStream is = conn.getInputStream();  
	            BufferedReader l_reader = new BufferedReader(new InputStreamReader(is)); 
	            while ((sCurrentLine = l_reader.readLine()) != null) {  
	            	result.append(sCurrentLine);  
	            }  
	            is.close();
	            isSuccess = true;
	            Log.i("result=", result.toString());  
	        } catch (Exception e) {  
	            e.printStackTrace(); 
	            isSuccess = false;
	            Intent it = new Intent();
                it.setAction("VCOM.UPLOADFILE.HTTP.RESULT");
                it.putExtra("result", "-1");
                it.putExtra("msg", "网络异常传输失败");
                context.sendBroadcast(it);
	            Log.i(Tag,"传输数据异常");
	        }finally{
	        	if(!isSuccess){
//	        		try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
	        		if(retryTimes >= 1){
						break;
					}else{
						retryTimes++;
					}
	        	}
	        }  
		}
		return result.toString();
	}

	//从startPosition处获取文件固定长度的数据
	private byte[] getBlockData(int startPositon){
		File f =new File(filePath);
		int length = (int)(fileLength - startPositon >= blockLength?blockLength:fileLength - startPositon);
		Log.i("length=", ""+length);
		byte[] blockData = new byte[length];
		if(f.exists()){
				try {
					FileInputStream is = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(is);
					bis.skip(startPositon);
					bis.read(blockData, 0,length);//(int)f.length()
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
	
	//使用广播机制抛出进度显示
	public void sendHandlerMessage(String fileName,int startPosition,int fileLength){
		Intent it = new Intent(PROGRESS_ACTION);
		it.putExtra("fileName", fileName);
		it.putExtra("startPosition", startPosition);
		it.putExtra("fileLength", fileLength);
		context.sendBroadcast(it);
		Log.i(Tag, "抛出进度显示");
	}

	public class NormalHandler extends DefaultHandler {
		private String preTag;
		private String result = "-1";
		
		public String getResult() {
			return result;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			String data = new String(ch, start, length).trim();
	        if(data != null){
	           if("string".equals(preTag)){
	        	   result = data;
	           }
	        }
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			preTag = null;
		}

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			preTag = localName;
		}
	}

	//解析只包含<string></string>节点的响应包
	public String analyzeNormalResponse(String responseStr){
		SAXParserFactory factory=SAXParserFactory.newInstance();
		NormalHandler singInOutHandler = new NormalHandler();  
		try {
			SAXParser parser = factory.newSAXParser();
	        parser.parse(new InputSource(new StringReader(responseStr)), singInOutHandler);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		return singInOutHandler.getResult();
	}
	
	//上传文件 对应的 pathurl
	public String getHttpUrl(String date,String mobile){
		if(date == null || date.equals("")){
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		String[] dateArray = date.split("-");
//		SharedPreferences sp = context.getSharedPreferences("communication_config", Context.MODE_PRIVATE);
		StringBuffer buffer = new StringBuffer();
//		String httpUploadURl = sp.getString("HTTPUPLOADURL", "http://220.162.239.101:9116/service1.asmx");
		buffer.append("http://"+fileUrl);
		buffer.append("/UploadFile/");
		buffer.append(taskType);
		buffer.append("/"+dateArray[0]+dateArray[1]+"/"+dateArray[2]+"/"+mobile+"/");
		buffer.append("HTTP/");
		buffer.toString();
		return buffer.toString();
	}
}
