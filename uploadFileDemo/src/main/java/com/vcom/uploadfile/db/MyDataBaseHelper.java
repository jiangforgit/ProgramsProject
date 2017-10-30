/**
 *	��Ȩ����  (c)2012,   V-com��<p>	
 *  �ļ�����	��MyDataBaseHelper.java<p>
 *
 *  ����ժҪ	��db������<p>
 *
 *  ����	��HP
 *  ����ʱ��	��2012-3-27 ����3:50:26 
 *  ��ǰ�汾�ţ�v1.0
 *  ��ʷ��¼	:
 *  ����	: 2012-3-27 ����3:50:26 	�޸��ˣ�
 *  ����	:
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
 *	��Ȩ����  (c)2012,   V-com��<p>	
 *  �ļ�����	��MyDataBaseHelper.java<p>
 *
 *  ����ժҪ	��db������<p>
 *
 *  ����	���� ��
 *  ����ʱ��	��2012-3-27 ����3:50:26 
 *  ��ǰ�汾�ţ�v1.0
 *  ��ʷ��¼	:
 *  ����	: 2012-3-27 ����3:50:26 	�޸��ˣ��� ��
 *  ����	:�����ͳ�ʼ�������ϵ�sqlite���ݿ⡣
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
	 *  �������� : onCreate
	 *  �������� :  �������ݿ�
	 *  ����˵����
	 *  	@param arg0
	 *  ����ֵ��
	 *  	
	 *  �޸ļ�¼��
	 *  ���ڣ�2012-3-27 ����3:50:26	�޸��ˣ��� ��
	 *  ����	��
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
	 *  �������� : onUpgrade
	 *  �������� :  �������ݿ�
	 *  ����˵����
	 *  	@param arg0
	 *  	@param arg1
	 *  	@param arg2
	 *  ����ֵ��
	 *  	
	 *  �޸ļ�¼��
	 *  ���ڣ�2012-3-27 ����3:50:26	�޸��ˣ��� ��
	 *  ����	��
	 * 					
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	//������url��ַ
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
