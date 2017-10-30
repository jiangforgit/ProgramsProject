package com.vcom.uploadfile.activity;

import java.io.File;
import java.util.UUID;

import com.vcom.uploadfile.enums.EnumContentType;
import com.vcom.uploadfile.interfaces.UploadFileService;
import com.vcom.uploadfile.util.MD5Util;
import com.vcom.ycwl.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class InsuranceScheduleDealActivity extends Activity {

	private static final String Tag = "InsuranceScheduleDealActivity";
	public static final String taskType = "test";
	private String packetId;//get task by this packetId
	private Uri mediaUri;//媒体资源对应的uri
	private String fileName;
	public static final int IMG_REQUEST_CODE = 0;
	private Button bt;
	private Button bt_show;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0){
				Intent it = new Intent(InsuranceScheduleDealActivity.this,FileUploadProgressActivity.class);
				startActivity(it);
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance_schedule_deal);
		initView();
		initData();
		setListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(Tag, "onActivityResult");
		if(resultCode != Activity.RESULT_OK){
			Log.i(Tag, "resultActivity关闭异常");
		}else{
			if(requestCode == IMG_REQUEST_CODE){
				Log.i(Tag, "成功拍照的result返回");
				PhotoDealUtil.savePhoto(MyApplication.IMAGES_SAVEPATH+fileName, 
						PhotoDealUtil.watermarkBitmap(PhotoDealUtil.rotaingImageView(PhotoDealUtil.readPictureDegree(MyApplication.IMAGES_SAVEPATH+fileName), PhotoDealUtil.scaleDrawableByFixedLen(MyApplication.IMAGES_SAVEPATH+fileName)), BitmapFactory.decodeResource(getResources(), R.drawable.picc_logo), "水印文字"));
				mHandler.sendEmptyMessageDelayed(0, 500);
				UploadFileService.getInstance().uploadFile(MyApplication.getInstance(),"orderId",UUID.randomUUID().toString().replaceAll("-", ""),MyApplication.IMAGES_SAVEPATH+fileName,"orderReason","contentValue",taskType,"com.vcom.uploadfile.UploadProgressReceiver","10086","2.0",EnumContentType.Photo.getType(),"jpg",false);
			}
		}
	}

	public void initView(){
		bt = (Button)findViewById(R.id.bt);
		bt_show = (Button)findViewById(R.id.bt_show);
	}
	
	public void initData(){
		packetId = UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public void setListener(){
		
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//拍照处理
				// TODO Auto-generated method stub
//				Bitmap bitmap = PhotoDealUtil.rotaingImageView(PhotoDealUtil.readPictureDegree(MyApplication.IMAGES_SAVEPATH+"test1.jpg"), PhotoDealUtil.scaleDrawableByFixedLen(MyApplication.IMAGES_SAVEPATH+"test1.jpg"));
//				if(bitmap.isRecycled()){
//					Log.i("bitmap", "recycled");
//				}
//				PhotoDealUtil.watermarkBitmap(bitmap, BitmapFactory.decodeResource(getResources(), R.drawable.picc_logo),"水印文字");
//				PhotoDealUtil.savePhoto(MyApplication.IMAGES_SAVEPATH+"test11.jpg", 
//						PhotoDealUtil.watermarkBitmap(PhotoDealUtil.rotaingImageView(PhotoDealUtil.readPictureDegree(MyApplication.IMAGES_SAVEPATH+"test1.jpg"), PhotoDealUtil.scaleDrawableByFixedLen(MyApplication.IMAGES_SAVEPATH+"test1.jpg")), BitmapFactory.decodeResource(getResources(), R.drawable.picc_logo), "水印文字"));
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					Toast.makeText(InsuranceScheduleDealActivity.this, "手机未安装sd卡", Toast.LENGTH_SHORT).show();
				}else{
					mediaUri = Uri.fromFile(new File(MyApplication.IMAGES_SAVEPATH, UUID.randomUUID().toString().replaceAll("-", "")+".jpg"));
					fileName = mediaUri.getLastPathSegment();//获取文件名
					Log.i(Tag, "文件名="+fileName);
					Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//根据提供的uri将拍下的照片进行存储
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mediaUri);
					startActivityForResult(intent, IMG_REQUEST_CODE);//requestCode = 0
				}
			}
		});
		bt_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(InsuranceScheduleDealActivity.this,FileUploadProgressActivity.class);
				startActivity(it);
			}
		});
	}
	
}
