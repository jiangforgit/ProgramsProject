/**
 *	版权所有  (c)2012,   V-com。<p>	
 *  文件名称	：MyDataBaseHelper.java<p>
 *
 *  内容摘要	：db助手类<p>
 *
 *  作者	：HP
 *  创建时间	：2012-3-27 下午3:50:26 
 *  当前版本号：v1.0
 *  历史记录	:
 *  日期	: 2012-3-27 下午3:50:26 	修改人：
 *  描述	:
 */
package com.vcom.uploadfile.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.vcom.uploadfile.domain.TableUploadUrl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *	版权所有  (c)2012,   V-com。<p>	
 *  文件名称	：MyDataBaseHelper.java<p>
 *
 *  内容摘要	：db助手类<p>
 *
 *  作者	：黄 菲
 *  创建时间	：2012-3-27 下午3:50:26 
 *  当前版本号：v1.0
 *  历史记录	:
 *  日期	: 2012-3-27 下午3:50:26 	修改人：黄 菲
 *  描述	:创建和初始化本地上的sqlite数据库。
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {

	private Context mContext;
	private static MyDataBaseHelper instance;
	private static int mDbVersion;
	private static String mDbName = ""; 
	
	public MyDataBaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	private MyDataBaseHelper(Context context) {
		super(context, mDbName,  null, mDbVersion);
		this.mContext = context;
	}

	
	public MyDataBaseHelper(Context context, String name, int version){
	
		this(context,name,null,version);
		this.mContext = context;
	}

	/**
	 *  函数名称 : onCreate
	 *  功能描述 :  创建数据库
	 *  参数说明：
	 *  	@param arg0
	 *  返回值：
	 *  	
	 *  修改记录：
	 *  日期：2012-3-27 下午3:50:26	修改人：黄 菲
	 *  描述	：
	 * 					
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		ArrayList<String> arrayList = new ArrayList<String>();
		InputStream inputStreams;
		BufferedReader bufReader;
		try {
				inputStreams = mContext.getAssets().open("FastSale.sql");
				bufReader = new BufferedReader(new InputStreamReader(inputStreams));
				String strSQL = null;
				String strAllString = "";
				String[] strsSQL = null;
				while((strSQL = bufReader.readLine())!=null){
					arrayList.add(strSQL);
				}
				for(int i=0;i<arrayList.size();i++)
				{
					strAllString = strAllString+arrayList.get(i);
				}
				strsSQL = strAllString.split(";");
				for(int j=0;j<strsSQL.length;j++)
				{
					db.execSQL(strsSQL[j]);
				}
//				insertUploadUrl(db);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  函数名称 : onUpgrade
	 *  功能描述 :  更新数据库
	 *  参数说明：
	 *  	@param arg0
	 *  	@param arg1
	 *  	@param arg2
	 *  返回值：
	 *  	
	 *  修改记录：
	 *  日期：2012-3-27 下午3:50:26	修改人：黄 菲
	 *  描述	：
	 * 					
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	//服务器url地址
	public void insertUploadUrl(SQLiteDatabase db){
		TableUploadUrl url = new TableUploadUrl();
		url.setIP("220.162.239.101");
		url.setPORT(27777);
		url.setFILE_URL("220.162.239.101");
		url.setUPLOAD_COUNT(0);
		url.setUPLOAD_TYPE(1);
		TableUploadUrl url1 = new TableUploadUrl();
		url1.setIP("http://220.162.239.101:9115/Service1.asmx");
		url1.setPORT(0);
		url1.setFILE_URL("220.162.239.101");
		url1.setUPLOAD_COUNT(0);
		url1.setUPLOAD_TYPE(2);
		for(int i=0;i<3;i++){
			db.execSQL("insert into VCOM_UPLOAD_URL (IP,PORT,FILE_URL,UPLOAD_COUNT,UPLOAD_TYPE,CREATE_TIME,IS_DIE)"
					  +"values(?,?,?,?,?,?,?)",
					  new Object[] {url.getIP(),url.getPORT(),url.getFILE_URL(),url.getUPLOAD_COUNT(),url.getUPLOAD_TYPE(),url.getCREATE_TIME(),url.getIS_DIE()});
			db.execSQL("insert into VCOM_UPLOAD_URL (IP,PORT,FILE_URL,UPLOAD_COUNT,UPLOAD_TYPE,CREATE_TIME,IS_DIE)"
					  +"values(?,?,?,?,?,?,?)",
					  new Object[] {url1.getIP(),url1.getPORT(),url1.getFILE_URL(),url1.getUPLOAD_COUNT(),url1.getUPLOAD_TYPE(),url1.getCREATE_TIME(),url1.getIS_DIE()});
		}
	}
	
	
}
