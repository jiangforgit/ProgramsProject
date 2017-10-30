package com.vcom.uploadfile.activity;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

public class MyApplication extends Application {

	private static MyApplication instance = null;
	public static String phoneNumber = null;//需要绑定的手机号
	public static String userId = "rbbj2";
	//应用的资源根目录
	public static final String APPLICATION_PATH = Environment.getExternalStorageDirectory()+"/";
	public static final String IMAGES_SAVEPATH = APPLICATION_PATH+"ycwl/";//图片资源目录
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
	
	//创建文件目录以保存文件资源
	public void createFolds(){
		//检查手机中是否配备安装sd卡
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "手机未安装sd卡", Toast.LENGTH_SHORT).show();
		}else{
			File file = new File(IMAGES_SAVEPATH);
			if(!file.exists()){
				file.mkdirs();
			}
		}
	}
}
