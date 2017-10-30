package com.vcom.uploadfile.activity;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

public class MyApplication extends Application {

	private static MyApplication instance = null;
	public static String phoneNumber = null;//��Ҫ�󶨵��ֻ���
	public static String userId = "rbbj2";
	//Ӧ�õ���Դ��Ŀ¼
	public static final String APPLICATION_PATH = Environment.getExternalStorageDirectory()+"/";
	public static final String IMAGES_SAVEPATH = APPLICATION_PATH+"ycwl/";//ͼƬ��ԴĿ¼
	public static final String PATH_URL = "http://220.162.239.101:9017/rbbj2/";
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		createFolds();
	}

	//get instance
	public static MyApplication getInstance(){
		return instance;
	}
	
	//�����ļ�Ŀ¼�Ա����ļ���Դ
	public void createFolds(){
		//����ֻ����Ƿ��䱸��װsd��
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "�ֻ�δ��װsd��", Toast.LENGTH_SHORT).show();
		}else{
			File file = new File(IMAGES_SAVEPATH);
			if(!file.exists()){
				file.mkdirs();
			}
		}
	}
}
