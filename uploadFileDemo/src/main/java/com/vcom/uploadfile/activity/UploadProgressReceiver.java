package com.vcom.uploadfile.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class UploadProgressReceiver extends BroadcastReceiver {

	private static final String Tag = "UploadProgressReceiver";
	public static String action = "com.vcom.uploadfile.UploadProgressReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        if(action.equals(intent.getAction())){//收到进度显示广播
        	if(FileUploadProgressActivity.isForeground){
        		String fileName = intent.getStringExtra("fileName");
            	int startPosition = intent.getIntExtra("startPosition", 0);
            	int fileLength = intent.getIntExtra("fileLength", 0);
            	Message msg = FileUploadProgressActivity.mHandler.obtainMessage(0);
        		Bundle bundle = new Bundle();
        		bundle.putString("fileName", fileName);
                bundle.putInt("startPosition", startPosition);
                bundle.putInt("fileLength", fileLength);
        		msg.setData(bundle);
        		FileUploadProgressActivity.mHandler.sendMessage(msg);
        	}
        }
	}

}
