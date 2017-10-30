package com.vcom.uploadfile.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class DrawableDealUtil {
	
	private static final String Tag = "DrawableDealUtil";

	private static DrawableDealUtil instance;
	
	private DrawableDealUtil(){
		
	}
	
	public static DrawableDealUtil getInstance(){
		if(instance == null){
			instance = new DrawableDealUtil();
		}
		return instance;
	}
	
	//按固定比例缩放
	public Bitmap scaleDrawableByFixedLen(String filePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;//不加载图片数据至内存至保存图片的某些参数
	    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options); //此时返回bm为空
	    options.inJustDecodeBounds = false;
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	    int be = (int)(options.outHeight / (float)400);
	    if (be <= 0)
	        be = 1;
	    options.inSampleSize = be;
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	    bitmap=BitmapFactory.decodeFile(filePath,options);
//	    int w = bitmap.getWidth();
//	    int h = bitmap.getHeight();
	    Log.i(Tag, "压缩图片");
	    return bitmap;
	}
	
	//按长宽各自比例进行缩放
	public Bitmap scaleDrawableByBounds(Bitmap bitmap,int sWidth,int sHeight){
		int bmpWidth  = bitmap.getWidth(); 
	    int bmpHeight  = bitmap.getHeight(); 
	    //缩放图片的尺寸 
	    float scaleWidth  = (float) sWidth / bmpWidth;     //按固定大小缩放  sWidth 写多大就多大
	    float scaleHeight = (float) sHeight / bmpHeight;  //
	    Matrix matrix = new Matrix(); 
	    matrix.postScale(scaleWidth, scaleHeight);//产生缩放后的Bitmap对象 
	    Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false); 
//	    Bitmap resizeBitmap = copy(bitmap);
	    bitmap.recycle(); 
	    return resizeBitmap;
	}
	
	//保存bitmap图片到具体地址
	public void saveImageFromBitmap(String savePath,String fileName,Bitmap bitmap){
		File file=new File(savePath,fileName);
		if(file.exists()){
			file.delete();
		}
	    try {
	        FileOutputStream out=new FileOutputStream(file);
        	if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){
	            out.flush();
	            out.close();
	        }
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }finally{
	    	bitmap.recycle();
	    }
	}
	
    /**
	* 根据原位图生成一个新的位图，并将原位图所占空间释放
	* @param srcBmp    原位图
	* @return          新位图
	*/
	public static Bitmap copy(Bitmap srcBmp){
		Bitmap destBmp=null;
		try{
			//创建一个临时文件
			File file = new File("/mnt/sdcard/temp/tmp.txt");
			file.getParentFile().mkdirs();
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw"); 
			int width = srcBmp.getWidth();
			int height = srcBmp.getHeight();
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, width*height*4);
			//将位图信息写进buffer
			srcBmp.copyPixelsToBuffer(map);
			//释放原位图占用的空间
			srcBmp.recycle();
			//创建一个新的位图
			destBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			map.position(0);
			//从临时缓冲中拷贝位图信息 
			destBmp.copyPixelsFromBuffer(map);
			channel.close();
			randomAccessFile.close();
		}
		catch(Exception ex){
			destBmp=null;
		}
		return destBmp;
	}
	
	//图片无损压缩
	public void qualityCompress(String filePath,Bitmap bitmap){
		File file=new File(filePath);
	    try {
	    	if(!file.exists()){
				file.createNewFile();
			}
	        FileOutputStream out=new FileOutputStream(file);
	        if(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)){
	        	out.flush();
	 	        out.close();
	        }
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }finally{
	    	bitmap.recycle();
	    }
	}
}
