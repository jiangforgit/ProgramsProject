package com.vcom.uploadfile.db;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.vcom.uploadfile.domain.FileUploadProgressInfo;
import com.vcom.uploadfile.domain.TableTaskContent;
import com.vcom.uploadfile.domain.TableUploadLog;
import com.vcom.uploadfile.domain.TableUploadUrl;
import com.vcom.uploadfile.enums.EnumContentType;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WorkOrderIUDS {
	public MyDataBaseHelper mDatabase;
	public WorkOrderIUDS instance;
	private Context mcon;
	public static final String DbName = "FastSale.dat";
	private int DbVersion = 12;
	
	private String uploadUrlSelect = "select IP,PORT,FILE_URL,UPLOAD_COUNT,UPLOAD_TYPE,CREATE_TIME,IS_DIE from VCOM_UPLOAD_URL ";
	private String uploadLogSelect = "select FILE_PATH,IP,PORT,FILE_URL,UPLOAD_TYPE,START_POSITION,UPLOAD_COUNT,CREATE_TIME from VCOM_UPLOAD_LOG ";
	
	public WorkOrderIUDS(Context context) {
		mcon = context;
		DbVersion = getDbVersion(context);
	}
	
	//��ȡ���ݿ�汾
	public int getDbVersion(Context context){
		SharedPreferences share = context.getSharedPreferences("Account",Context.MODE_PRIVATE);
		return Integer.parseInt(share.getString("dbVersion", "100"));
	}
	
	/***********************OrderContent��ҵ�����****************/
	//create taskContent
	public void createTaskContent(TableTaskContent taskContent){
		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("insert into VCOM_ORDER_CONTENT (PACKET_ID,ORDER_REASON,CONTENT_TYPE,CONTENT_NAME,CONTENT_VALUE,MARK,UPLOAD_STATUS,FILE_LENGTH,ORDER_ID,CREATE_TIME,SORT,UPDATE_TIME)"
				  +"values(?,?,?,?,?,?,?,?,?,?,?,?)",
				  new Object[] {taskContent.getPacketId(),taskContent.getOrderReason(),taskContent.getContentType(),taskContent.getContentName(),taskContent.getContentValue(),
						        taskContent.getMark(),taskContent.getUploadStatus(),taskContent.getFileLength(),taskContent.getOrderId(),taskContent.getDate(),taskContent.getSORT(),taskContent.getUPDATE_TIME()});
		if (db != null) {
			db.close();
		}
	}
	
	//�޸�starPosition��uploadStatus
	public void updateStartAndStatus(TableTaskContent taskContent){
		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_ORDER_CONTENT set START_POSITION = "+taskContent.getStartPosition()+", UPLOAD_STATUS = "+taskContent.getUploadStatus()+" where CONTENT_NAME = '"+taskContent.getContentName()+"'");
		if (db != null) {
			db.close();
		}
	}
	
	//�޸�uploadStatus
	public void updateUploadStatus(TableTaskContent taskContent){
		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_ORDER_CONTENT set UPLOAD_STATUS = "+taskContent.getUploadStatus()+" where CONTENT_NAME = '"+taskContent.getContentName()+"'");
		if (db != null) {
			db.close();
		}
	}
	
	//�޸�uploadStatus
	public void updateStartPosition(TableTaskContent taskContent){
		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_ORDER_CONTENT set START_POSITION = "+taskContent.getStartPosition()+" where CONTENT_NAME = '"+taskContent.getContentName()+"'");
		if (db != null) {
			db.close();
		}
	}
	
	//��ҳ��ȡ�ϴ�ͼƬ�ļ�����ʾ��¼ 
    public List<FileUploadProgressInfo> getAllFileUploadProgressInfos(int from,int counts){
    	List<FileUploadProgressInfo> infos = new ArrayList<FileUploadProgressInfo>();
    	String query = "select CONTENT_NAME,FILE_LENGTH,START_POSITION,MARK,MARKRESULT,UPLOAD_STATUS from VCOM_ORDER_CONTENT where (CONTENT_TYPE = "+EnumContentType.Photo.getType()+" or CONTENT_TYPE = "+EnumContentType.Voice.getType()+" or CONTENT_TYPE = "+EnumContentType.video.getType()+") and UPLOAD_STATUS < 2 order by UPLOAD_STATUS asc limit "+from+","+counts; 
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		while (cursor.moveToNext()) {
			FileUploadProgressInfo info = new FileUploadProgressInfo();
			info.setFileName(cursor.getString(cursor.getColumnIndex("CONTENT_NAME")));
			info.setFileLength(cursor.getInt(cursor.getColumnIndex("FILE_LENGTH")));
			info.setUploadedLength(cursor.getInt(cursor.getColumnIndex("MARKRESULT"))==1?cursor.getInt(cursor.getColumnIndex("START_POSITION")):cursor.getInt(cursor.getColumnIndex("MARK"))>>10);
			info.setUploadStatus(cursor.getInt(cursor.getColumnIndex("UPLOAD_STATUS")));
			infos.add(info);
		}
		cursor.close();
		db.close();
		return infos;
    }
    
    //��ȡ��Ҫ�ϵ������ļ�¼
    public List<TableTaskContent> getReUploadFileRecords(){
    	List<TableTaskContent> taskContents = new ArrayList<TableTaskContent>();
    	String query = "select ORDER_ID,PACKET_ID,CONTENT_NAME,CONTENT_TYPE from VCOM_ORDER_CONTENT where CONTENT_TYPE ="+EnumContentType.Photo.getType()+" and UPLOAD_STATUS = 0 order by ID desc limit 5";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		while (cursor.moveToNext()) {
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setOrderId(cursor.getString(cursor.getColumnIndex("ORDER_ID")));
			taskContent.setPacketId(cursor.getString(cursor.getColumnIndex("PACKET_ID")));
			taskContent.setContentName(cursor.getString(cursor.getColumnIndex("CONTENT_NAME")));
			taskContent.setContentType(cursor.getInt(cursor.getColumnIndex("CONTENT_TYPE")));
			taskContents.add(taskContent);
		}
		cursor.close();
		db.close();
		return taskContents;
    }  	

    //��ȡ��Ҫ�ϵ�������uploadstatus = 1 �ļ�¼
    public List<TableTaskContent> getReUploadUploading(){
    	List<TableTaskContent> taskContents = new ArrayList<TableTaskContent>();
    	String query = "select ORDER_ID,PACKET_ID,CONTENT_NAME,CONTENT_TYPE from VCOM_ORDER_CONTENT where CONTENT_TYPE ="+EnumContentType.Photo.getType()+" and UPLOAD_STATUS = 1 and UPDATE_TIME <"+(System.currentTimeMillis()-120000)+" order by ID desc limit 15";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		while (cursor.moveToNext()) {
			TableTaskContent taskContent = new TableTaskContent();
			taskContent.setOrderId(cursor.getString(cursor.getColumnIndex("ORDER_ID")));
			taskContent.setPacketId(cursor.getString(cursor.getColumnIndex("PACKET_ID")));
			taskContent.setContentName(cursor.getString(cursor.getColumnIndex("CONTENT_NAME")));
			taskContent.setContentType(cursor.getInt(cursor.getColumnIndex("CONTENT_TYPE")));
			taskContents.add(taskContent);
		}
		cursor.close();
		db.close();
		return taskContents;
    }  	
    
    //get startPosition 
    public int getStartPositionByFileName(String fileName){
    	int result = -1;
    	String query = "select START_POSITION from VCOM_ORDER_CONTENT where CONTENT_NAME = '"+fileName+"'";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if(cursor.moveToFirst()){
			result = cursor.getInt(cursor.getColumnIndex("START_POSITION"));
		}
		cursor.close();
		db.close();
    	return result;
    }

    //get date
    public String getCreateTimeByFileName(String fileName){
    	String result = "";
    	String query = "select CREATE_TIME from VCOM_ORDER_CONTENT where CONTENT_NAME = '"+fileName+"'";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if(cursor.moveToFirst()){
			result = cursor.getString(cursor.getColumnIndex("CREATE_TIME"));
		}
		cursor.close();
		db.close();
    	return result;
    }
    
    //��֤ĳһ�ļ���¼�Ƿ��Ѿ�����
    public boolean isFileExists(String fileName){
    	boolean result = false;
    	String query = "select CONTENT_NAME from VCOM_ORDER_CONTENT where CONTENT_NAME = '"+fileName+"'";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if(cursor.getCount()>0){
			result = true;
		}
		cursor.close();
		db.close();
    	return result;
    }

    //��ȡhttp�ϴ�λ�� mark
    public int getMarkByFileName(String fileName){
    	int result = -1;
    	String query = "select MARK from VCOM_ORDER_CONTENT where CONTENT_NAME = '"+fileName+"'";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if(cursor.moveToFirst()){
			result = cursor.getInt(cursor.getColumnIndex("MARK"));
		}
		cursor.close();
		db.close();
    	return result;
    }
    
    //�޸�mark
    public void updateMarkByFileName(int mark,String fileName){
		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_ORDER_CONTENT set MARK = "+mark+" where CONTENT_NAME = '"+fileName+"'");
		if (db != null) {
			db.close();
		}
    }
    
  //�޸�markResult
    public void updateMarkResultByFileName(int markResult,String fileName){
		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_ORDER_CONTENT set MARKRESULT = "+markResult+" where CONTENT_NAME = '"+fileName+"'");
		if (db != null) {
			db.close();
		}
    }
    
    //��ȡhttp�ϴ�λ�� markResult
    public int getMarkResultByFileName(String fileName){
    	int result = -1;
    	String query = "select MARKRESULT from VCOM_ORDER_CONTENT where CONTENT_NAME = '"+fileName+"'";
    	MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if(cursor.moveToFirst()){
			result = cursor.getInt(cursor.getColumnIndex("MARKRESULT"));
		}
		cursor.close();
		db.close();
    	return result;
    }
    
  //�޸�mark��uploadStatus
  	public void updateMarkAndStatus(int mark,int uploadStatus,String fileName){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
  		db.execSQL("update VCOM_ORDER_CONTENT set MARK = "+mark+", UPLOAD_STATUS = "+uploadStatus+" where CONTENT_NAME = '"+fileName+"'");
  		if (db != null) {
  			db.close();
  		}
  	}
    
  	//����fileName�޸�fileRootUrl
  	public void updateFileRootUrlByFileName(String fileRootUrl,String fileName){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
  		db.execSQL("update VCOM_ORDER_CONTENT set FILE_ROOT_URL = '"+fileRootUrl+"' where CONTENT_NAME = '"+fileName+"'");
  		if (db != null) {
  			db.close();
  		}
  	}
  	
   //��ȡorder��Ӧ��ͼƬ��
  	public int getPhotoCountByOrderId(String orderId){
  		int result = 0;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getReadableDatabase();
  		String query = "select count(*) from VCOM_ORDER_CONTENT where " +
  				       "CONTENT_TYPE = "+EnumContentType.Photo.getType()+" and ORDER_ID = '"+orderId+"'";
  		Cursor cursor = db.rawQuery(query, null);
  		if(cursor.moveToFirst()){
  			result = cursor.getInt(0);
  		}
  		cursor.close();
  		db.close();
  		return result;
  	}
  	
  	//����packetId��ȡthreadId
  	public String getThreadIdByPacketId(String packetId){
  		String result = "";
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getReadableDatabase();
  		String query = "select THREAD_ID from VCOM_ORDER_CONTENT where PACKET_ID = '"+packetId+"'";
  		Cursor cursor = db.rawQuery(query, null);
  		if(cursor.moveToFirst()){
  			result = cursor.getString(0);
  		}
  		cursor.close();
  		db.close();
  		return result;
  	}
  	
  	//update threadId
  	public void updateThreadIdByPacketId(String packetId,String threadId){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
  		db.execSQL("update VCOM_ORDER_CONTENT set THREAD_ID = ?  where PACKET_ID = ?", new Object[] {
  				threadId,packetId});
  		db.close();
  	}
  	
  	//update updateTime
  	public void updateUpdateTime(String packetId,long updateTime){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
  		db.execSQL("update VCOM_ORDER_CONTENT set UPDATE_TIME = ?  where PACKET_ID = ?", new Object[] {
  				updateTime,packetId});
  		db.close();
  	}
  	
    /*************************�ļ��ϴ�log��¼����******************/
  	public void uploadFileTest(String threadName,String fileName,int fileLength,String guid,String cmd,int startPosition,int packetges,String failurePackets){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("insert into uploadTest (threadName,fileName,fileLength,guid,cmd,startPosition,packetges,failurePackets)"
				  +"values(?,?,?,?,?,?,?,?)",
				  new Object[] {threadName,fileName,fileLength,guid,cmd,startPosition,packetges,failurePackets});
		if (db != null) {
			db.close();
		}
  	}

  	/*************************upload_url******************/
  	public void setTableUploadUrlValue(TableUploadUrl uploadUrl,Cursor cursor){
  		uploadUrl.setIP(cursor.getString(cursor.getColumnIndex("IP")));
  		uploadUrl.setPORT(cursor.getInt(cursor.getColumnIndex("PORT")));
  		uploadUrl.setFILE_URL(cursor.getString(cursor.getColumnIndex("FILE_URL")));
  		uploadUrl.setUPLOAD_COUNT(cursor.getInt(cursor.getColumnIndex("UPLOAD_COUNT")));
  		uploadUrl.setUPLOAD_TYPE(cursor.getInt(cursor.getColumnIndex("UPLOAD_TYPE")));
  		uploadUrl.setCREATE_TIME(cursor.getString(cursor.getColumnIndex("CREATE_TIME")));
  		uploadUrl.setIS_DIE(cursor.getInt(cursor.getColumnIndex("IS_DIE")));
	}
  	
  	public void createUploadUrl(TableUploadUrl uploadUrl){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("insert into VCOM_UPLOAD_URL (IP,PORT,FILE_URL,UPLOAD_COUNT,UPLOAD_TYPE,CREATE_TIME,IS_DIE)"
				  +"values(?,?,?,?,?,?,?)",
				  new Object[] {uploadUrl.getIP(),uploadUrl.getPORT(),uploadUrl.getFILE_URL(),uploadUrl.getUPLOAD_COUNT(),uploadUrl.getUPLOAD_TYPE(),uploadUrl.getCREATE_TIME(),uploadUrl.getIS_DIE()});
		if (db != null) {
			db.close();
		}
  	}
  	
  	//��ȡcount����С�ļ�¼
  	public TableUploadUrl getUploadUrlByCountMin(int uploadType){
  		TableUploadUrl uploadUrl = new TableUploadUrl();
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(uploadUrlSelect+"where UPLOAD_TYPE = "+uploadType+" and IS_DIE != 1 order by UPLOAD_COUNT asc", null);
		if(cursor.moveToFirst()){
			setTableUploadUrlValue(uploadUrl,cursor);
		}
		cursor.close();
		db.close();
		return uploadUrl;
  	}
  	
  	//update uploadCount
  	public void autoAddUrlUploadCount(String ip,int port,int uploadType){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_UPLOAD_URL set UPLOAD_COUNT = UPLOAD_COUNT+1 where IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+uploadType+"");
		if (db != null) {
			db.close();
		}
  	}
  	
  //��֤��Դ��ַ��¼���Ƿ� >0
  	public boolean isResExists(){
  		boolean result = false;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from VCOM_UPLOAD_URL", null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0){
				result = true;
			}
		}
		cursor.close();
		db.close();
		return result;
  	}
  	
  	//�������� ɾ�����������
  	public boolean updateUrlsByTask(List<TableUploadUrl> urls){
  		boolean result = false;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.beginTransaction();  
	    try  
	    {  
	    	db.execSQL("delete from VCOM_UPLOAD_URL");
	        for(TableUploadUrl uploadUrl:urls){
	        	db.execSQL("insert into VCOM_UPLOAD_URL (IP,PORT,FILE_URL,UPLOAD_COUNT,UPLOAD_TYPE,CREATE_TIME,IS_DIE)"
	  				  +"values(?,?,?,?,?,?,?)",
	  				  new Object[] {uploadUrl.getIP(),uploadUrl.getPORT(),uploadUrl.getFILE_URL(),uploadUrl.getUPLOAD_COUNT(),uploadUrl.getUPLOAD_TYPE(),uploadUrl.getCREATE_TIME(),uploadUrl.getIS_DIE()});
	        }
	        //���������־Ϊ�ɹ�������������ʱ�ͻ��ύ����  
	        db.setTransactionSuccessful(); 
	        result = true;
	    }catch(Exception e){
	        e.printStackTrace();
	    }finally{  
	        //��������  
	        db.endTransaction();  
	    } 
	    db.close();
	    return result;
  	}

  	//�޸�IS_DIE ״̬
  	public void updateIsDieByRes(String ip,int port, int type,int isDie){
  		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_UPLOAD_URL set CREATE_TIME = '"+currentTime+"',IS_DIE = "+isDie+" where IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+type+"");
		if (db != null) {
			db.close();
		}
  	}
  	
  	//��֤�Ƿ��б���ֹ�ķ�����
  	public boolean isInterdicted(){
  		boolean result = false;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		String sql = "select count(*) from VCOM_UPLOAD_URL where IS_DIE = 1";
		Cursor cursor = db.rawQuery(sql,null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0){
				result = true;
			}
		}
		cursor.close();
		db.close();
  		return result;
  	}
  	
  	//��ȡ����ֹ�ķ�������¼��ʱ�䳬�� 5���ӣ�
  	public List<TableUploadUrl> getInterdeicteds(){
  		List<TableUploadUrl> urls = new ArrayList<TableUploadUrl>();
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(uploadUrlSelect+"where IS_DIE = 1", null);
		while(cursor.moveToNext()){
			TableUploadUrl uploadUrl = new TableUploadUrl();
			setTableUploadUrlValue(uploadUrl,cursor);
			urls.add(uploadUrl);
		}
		cursor.close();
		db.close();
		return urls;
  	}
  	
  	public void updateUrlCurrentTimeByRes(String ip,int port, int type){
  		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_UPLOAD_URL set CREATE_TIME = '"+currentTime+"' where IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+type+"");
		if (db != null) {
			db.close();
		}
  	}
  	
  	/*************************upload_log******************/
  	public void setTableUploadLogValue(TableUploadLog uploadLog,Cursor cursor){
  		uploadLog.setFILE_PATH(cursor.getString(cursor.getColumnIndex("FILE_PATH")));
  		uploadLog.setIP(cursor.getString(cursor.getColumnIndex("IP")));
  		uploadLog.setPORT(cursor.getInt(cursor.getColumnIndex("PORT")));
  		uploadLog.setFILE_URL(cursor.getString(cursor.getColumnIndex("FILE_URL")));
  		uploadLog.setUPLOAD_TYPE(cursor.getInt(cursor.getColumnIndex("UPLOAD_TYPE")));
  		uploadLog.setSTART_POSITION(cursor.getInt(cursor.getColumnIndex("START_POSITION")));
  		uploadLog.setUPLOAD_COUNT(cursor.getInt(cursor.getColumnIndex("UPLOAD_COUNT")));
  		uploadLog.setCREATE_TIME(cursor.getString(cursor.getColumnIndex("CREATE_TIME")));
	}
  	
  	public void createUploadLog(TableUploadLog uploadLog){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("insert into VCOM_UPLOAD_LOG (FILE_PATH,IP,PORT,FILE_URL,UPLOAD_TYPE,START_POSITION,UPLOAD_COUNT,CREATE_TIME)"
				  +"values(?,?,?,?,?,?,?,?)",
				  new Object[] {uploadLog.getFILE_PATH(),uploadLog.getIP(),uploadLog.getPORT(),uploadLog.getFILE_URL(),uploadLog.getUPLOAD_TYPE(),uploadLog.getSTART_POSITION(),uploadLog.getUPLOAD_COUNT(),uploadLog.getCREATE_TIME()});
		if (db != null) {
			db.close();
		}
  	}
  	
  	//��ȡlog����ʷ�ϴ���¼ by type �� count <= 3  startPosition ���ļ�¼
  	public TableUploadLog getFileHistoryUploadLog(String filePath,int type){
  		TableUploadLog uploadLog = new TableUploadLog();
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery(uploadLogSelect+"where FILE_PATH = '"+filePath+"' and UPLOAD_TYPE = "+type+" and UPLOAD_COUNT < 3 order by START_POSITION desc", null);
		if(cursor.moveToFirst()){
			setTableUploadLogValue(uploadLog,cursor);
		}
		cursor.close();
		db.close();
		return uploadLog;
  	}
  	
  	//���� filePath ip port type ��֤��Ӧ��log��¼�Ƿ����
  	public boolean isUploadLogExists(String filePath,String ip,int port,int type){
  		boolean result = false;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from VCOM_UPLOAD_LOG where FILE_PATH = '"+filePath+"' and IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+type+"", null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0){
				result = true;
			}
		}
		cursor.close();
		db.close();
  		return result;
  	}
  	
  	//����filePath ip port type ���� upload_count
  	public void autoAddLogUploadCount(String filePath,String ip,int port,int type){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_UPLOAD_LOG set UPLOAD_COUNT = UPLOAD_COUNT+1 where FILE_PATH = '"+filePath+"' and IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+type+"");
		if (db != null) {
			db.close();
		}
  	}
  	
  	//update startPosition
  	public void updateLogStartPosition(String filePath,String ip,int port,int type,int statrPosition){
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
  		SQLiteDatabase db = dbh.getWritableDatabase();
		db.execSQL("update VCOM_UPLOAD_LOG set START_POSITION = "+statrPosition+" where FILE_PATH = '"+filePath+"' and IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+type+"");
		if (db != null) {
			db.close();
		}
  	}
  	
  	//��֤ �Ƿ���Ҫ���ϴ����ļ��������ٽ��Ͷ�����
  	public boolean isRegulate(){
  		boolean result = false;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		String sql = "select count(*) from VCOM_UPLOAD_LOG where FILE_PATH in (select CONTENT_NAME from VCOM_ORDER_CONTENT where UPLOAD_STATUS = 2 and CONTENT_TYPE = "+EnumContentType.Photo.getType()+" order by ID desc limit 2) and UPLOAD_COUNT >=2";
		Cursor cursor = db.rawQuery(sql,null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) > 0){
				result = true;
			}
		}
		cursor.close();
		db.close();
  		return result;
  	}
  	
  	//��ȡ��־��¼ �ش�����
  	public int getUploadCountByRes(String filePath,String ip,int port,int type){
  		int result = 0;
  		MyDataBaseHelper dbh = new MyDataBaseHelper(mcon, DbName, DbVersion);
		SQLiteDatabase db = dbh.getReadableDatabase();
		String sql = "select UPLOAD_COUNT from VCOM_UPLOAD_LOG where FILE_PATH = '"+filePath+"' and IP='"+ip+"' and PORT = "+port+" and UPLOAD_TYPE = "+type+"";
		Cursor cursor = db.rawQuery(sql,null);
		if(cursor.moveToFirst()){
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
  	}
  	
}
