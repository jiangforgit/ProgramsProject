package com.vcom.uploadfile.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

public class PhotoDealUtil {

	public static Bitmap readBitmapAutoSize(String filePath) {    
        //outWidth��outHeight��Ŀ��ͼƬ������Ⱥ͸߶ȣ���������  
		FileInputStream fs = null;  
		BufferedInputStream bs = null;  
		try {  
		    fs = new FileInputStream(filePath);  
		    bs = new BufferedInputStream(fs);  
		    BitmapFactory.Options options = setBitmapOption(filePath);  
		    return BitmapFactory.decodeStream(bs, null, options);  
		} catch (Exception e) {  
		    e.printStackTrace();  
		} finally {  
		    try {  
		        bs.close();  
		        fs.close();  
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
		}  
		return null;  
	}  
	
	private static BitmapFactory.Options setBitmapOption(String file) {  
        BitmapFactory.Options opt = new BitmapFactory.Options();  
//        opt.inJustDecodeBounds = true;            
        //����ֻ�ǽ���ͼƬ�ı߾࣬�˲���Ŀ���Ƕ���ͼƬ��ʵ�ʿ�Ⱥ͸߶�  
//        BitmapFactory.decodeFile(file, opt);
//        int outWidth = opt.outWidth; //���ͼƬ��ʵ�ʸߺͿ�  
//        int outHeight = opt.outHeight;  
//        opt.inDither=false; //ͼƬ������
//        opt.inPurgeable=true; //ʹ���ڴ���Ա�����
//        opt.inInputShareable = true; 
//        opt.inTempStorage=new byte[1024]; //��ʱ�洢 
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;      
        //���ü���ͼƬ����ɫ��Ϊ16bit��Ĭ����RGB_8888����ʾ24bit��ɫ��͸��ͨ������һ���ò���  
        File f = new File(file);
        long length = f.length();
        if(length < 2<<20){
        	opt.inSampleSize = 1; 
        }else if(length >= 2<<20 && length < 3<<20){
        	opt.inSampleSize = 1; 
        }else if(length >= 3<<20 && length < 4<<20){
        	opt.inSampleSize = 1; 
        }else if(length >= 4<<20 && length < 6<<20){
        	opt.inSampleSize = 1;
        }else if(length >= 6<<20){
        	opt.inSampleSize = 2;
        }       
        //�������ű�,1��ʾԭ������2��ʾԭ�����ķ�֮һ....  
        //�������ű�  
//        if (outWidth != 0 && outHeight != 0) {  
//            int sampleSize = (outWidth / 2048 + outHeight / 1536) / 2;  
//            opt.inSampleSize = sampleSize;  
//        }  
        opt.inJustDecodeBounds = false;//���ѱ�־��ԭ  
        return opt;  
    }  
	
	
	//��ȡbitmap��������Ϣ
	public static int[] getBitmapColors(Bitmap bitmap){
		int mBitmapWidth = bitmap.getWidth();
		int mBitmapHeight = bitmap.getHeight();
		int[] mArrayColor = new int[mBitmapWidth*mBitmapHeight];  
        int count = 0;  
        for (int i = 0; i < mBitmapHeight; i++) {  
        for (int j = 0; j < mBitmapWidth; j++) {  
            //���Bitmap ͼƬ��ÿһ�����color��ɫֵ  
            int color = bitmap.getPixel(j, i);  
            //����ɫֵ����һ�������� ��������޸�  
            mArrayColor[count] = color;  
            //����������ĸ�ϸ�µĻ� ���԰���ɫֵ��R G B �õ�����Ӧ�Ĵ��� ����������Ͳ����������  
//            int r = Color.red(color);  
//            int g = Color.green(color);  
//            int b = Color.blue(color); 
            count++;  
        }  
       } 
        bitmap.recycle();
        bitmap = null;
        System.gc();
       return mArrayColor;
	}
	
	
	//���̶���������
	public static Bitmap scaleDrawableByFixedLen(String filePath){
		return readBitmapAutoSize(filePath);
//		BitmapFactory.Options options = new BitmapFactory.Options();
//	    options.inJustDecodeBounds = true;//������ͼƬ�������ڴ�������ͼƬ��ĳЩ����
//	    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options); //��ʱ����bmΪ��
//	    options.inJustDecodeBounds = false;
//	    //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
//	    int be = (int)(options.outHeight / (float)320);
//	    if (be <= 0)
//	        be = 1;
//	    options.inSampleSize = be;
//	    //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
//	    bitmap=BitmapFactory.decodeFile(filePath,options);
////		    int w = bitmap.getWidth();
////		    int h = bitmap.getHeight();
	}
	
	
	//��������Ա�����������
	public static Bitmap scaleDrawableByBounds(Bitmap bitmap,int sWidth,int sHeight){
		int bmpWidth  = bitmap.getWidth(); 
	    int bmpHeight  = bitmap.getHeight(); 
	    //����ͼƬ�ĳߴ� 
	    float scaleWidth  = (float) sWidth / bmpWidth;     //���̶���С����  sWidth д���Ͷ��
	    float scaleHeight = (float) sHeight / bmpHeight;  //
	    Matrix matrix = new Matrix(); 
	    matrix.postScale(scaleWidth, scaleHeight);//�������ź��Bitmap���� 
	    Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false); 
	    bitmap.recycle(); 
	    bitmap = null;
	    System.gc();
	    return resizeBitmap;
	}
	
    // ��ˮӡ Ҳ���Լ�����
    public static Bitmap watermarkBitmap(Bitmap src, Bitmap watermark,String title) {
        if (src == null) {
            return null;
        }
//        int[] colors = getBitmapColors(src);
        int w = src.getWidth();
        int h = src.getHeight(); 
        //��Ҫ����ͼƬ̫����ɵ��ڴ泬��������,�����ҵ�ͼƬ��С���Բ�д��Ӧ������        
        Bitmap newb= Bitmap.createBitmap(w, h, Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// �� 0��0���꿪ʼ����src  
//        cv.drawBitmap(colors, 0, w, 0, 0, w, h, true, null);
        src.recycle();
        src = null;
        System.gc();
        Paint paint=new Paint();
        //����ͼƬ
        if (watermark != null) {
            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
            paint.setAlpha(50);
            cv.drawBitmap(watermark, w - (ww + 10), h - (wh + 10), paint);// ��src�����½ǻ���ˮӡ            
        }
        //��������
        if(title!=null)
        {
            String familyName ="����";
            Typeface font = Typeface.create(familyName,Typeface.BOLD);            
            TextPaint textPaint=new TextPaint();
            textPaint.setColor(Color.argb(123, 255,0,0));
            textPaint.setTypeface(font);
            textPaint.setTextSize(35);
            //�������Զ����е�
            StaticLayout layout = new StaticLayout(title,textPaint,w,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
            layout.draw(cv);
            //���־ͼ����Ͻ�����
            //cv.drawText(title,0,40,paint); 
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// ����
        cv.restore();// �洢
        return newb;
    }
    
    /**
     * 1�����ж��ֻ����յ���ת�Ƕ�
     * ��ȡͼƬ���ԣ���ת�ĽǶ�
     * @param path
     * ͼƬ����·��
     * @return degree��ת�ĽǶ�
     */
   public static int readPictureDegree(String path) {
     int degree = 0;
     try {
      ExifInterface exifInterface = new ExifInterface(path);
      int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
      case
      ExifInterface.ORIENTATION_ROTATE_90:
	      degree = 90;
	      break;
      case ExifInterface.ORIENTATION_ROTATE_180:
	      degree = 180;
	      break;
      case ExifInterface.ORIENTATION_ROTATE_270:
	      degree = 270;
	      break;
      }
     } catch (IOException e) {
      e.printStackTrace();
     }
     return degree;
   }

   /**
    * 2�������ɵ���Ƭ������ת������ת����
     * ��תͼƬ
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
   public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
     // ��תͼƬ ����
	 Bitmap resizedBitmap = null;
	 if(angle != 0){
		 Matrix matrix = new Matrix();
	     matrix.postRotate(angle);
	     // �����µ�ͼƬ
	     resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix, true);
	     bitmap.recycle();
	     bitmap = null;
	     System.gc();
	 }else{
		 resizedBitmap = bitmap;
	 }
     return resizedBitmap; 
   }
   

   public static void savePhoto(String mCurrentPhotoPath,Bitmap bitmap) {
   	    File file=new File(mCurrentPhotoPath);
		if(file.exists()){
			file.delete();
		}
	    try {
	        FileOutputStream out=new FileOutputStream(file);
	        if(bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)){
	            out.flush();
	            out.close();
	            bitmap.recycle();
	            bitmap = null;
	            System.gc();
	        }
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
   }
   
}
