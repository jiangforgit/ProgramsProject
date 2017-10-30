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
	
	//���̶���������
	public Bitmap scaleDrawableByFixedLen(String filePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;//������ͼƬ�������ڴ�������ͼƬ��ĳЩ����
	    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options); //��ʱ����bmΪ��
	    options.inJustDecodeBounds = false;
	    //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
	    int be = (int)(options.outHeight / (float)400);
	    if (be <= 0)
	        be = 1;
	    options.inSampleSize = be;
	    //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
	    bitmap=BitmapFactory.decodeFile(filePath,options);
//	    int w = bitmap.getWidth();
//	    int h = bitmap.getHeight();
	    Log.i(Tag, "ѹ��ͼƬ");
	    return bitmap;
	}
	
	//��������Ա�����������
	public Bitmap scaleDrawableByBounds(Bitmap bitmap,int sWidth,int sHeight){
		int bmpWidth  = bitmap.getWidth(); 
	    int bmpHeight  = bitmap.getHeight(); 
	    //����ͼƬ�ĳߴ� 
	    float scaleWidth  = (float) sWidth / bmpWidth;     //���̶���С����  sWidth д���Ͷ��
	    float scaleHeight = (float) sHeight / bmpHeight;  //
	    Matrix matrix = new Matrix(); 
	    matrix.postScale(scaleWidth, scaleHeight);//�������ź��Bitmap���� 
	    Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false); 
//	    Bitmap resizeBitmap = copy(bitmap);
	    bitmap.recycle(); 
	    return resizeBitmap;
	}
	
	//����bitmapͼƬ�������ַ
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
	* ����ԭλͼ����һ���µ�λͼ������ԭλͼ��ռ�ռ��ͷ�
	* @param srcBmp    ԭλͼ
	* @return          ��λͼ
	*/
	public static Bitmap copy(Bitmap srcBmp){
		Bitmap destBmp=null;
		try{
			//����һ����ʱ�ļ�
			File file = new File("/mnt/sdcard/temp/tmp.txt");
			file.getParentFile().mkdirs();
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw"); 
			int width = srcBmp.getWidth();
			int height = srcBmp.getHeight();
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, width*height*4);
			//��λͼ��Ϣд��buffer
			srcBmp.copyPixelsToBuffer(map);
			//�ͷ�ԭλͼռ�õĿռ�
			srcBmp.recycle();
			//����һ���µ�λͼ
			destBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			map.position(0);
			//����ʱ�����п���λͼ��Ϣ 
			destBmp.copyPixelsFromBuffer(map);
			channel.close();
			randomAccessFile.close();
		}
		catch(Exception ex){
			destBmp=null;
		}
		return destBmp;
	}
	
	//ͼƬ����ѹ��
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
