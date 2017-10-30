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
        //outWidth和outHeight是目标图片的最大宽度和高度，用作限制  
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
        //设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度  
//        BitmapFactory.decodeFile(file, opt);
//        int outWidth = opt.outWidth; //获得图片的实际高和宽  
//        int outHeight = opt.outHeight;  
//        opt.inDither=false; //图片不抖动
//        opt.inPurgeable=true; //使得内存可以被回收
//        opt.inInputShareable = true; 
//        opt.inTempStorage=new byte[1024]; //临时存储 
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;      
        //设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上  
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
        //设置缩放比,1表示原比例，2表示原来的四分之一....  
        //计算缩放比  
//        if (outWidth != 0 && outHeight != 0) {  
//            int sampleSize = (outWidth / 2048 + outHeight / 1536) / 2;  
//            opt.inSampleSize = sampleSize;  
//        }  
        opt.inJustDecodeBounds = false;//最后把标志复原  
        return opt;  
    }  
	
	
	//获取bitmap的像素信息
	public static int[] getBitmapColors(Bitmap bitmap){
		int mBitmapWidth = bitmap.getWidth();
		int mBitmapHeight = bitmap.getHeight();
		int[] mArrayColor = new int[mBitmapWidth*mBitmapHeight];  
        int count = 0;  
        for (int i = 0; i < mBitmapHeight; i++) {  
        for (int j = 0; j < mBitmapWidth; j++) {  
            //获得Bitmap 图片中每一个点的color颜色值  
            int color = bitmap.getPixel(j, i);  
            //将颜色值存在一个数组中 方便后面修改  
            mArrayColor[count] = color;  
            //如果你想做的更细致的话 可以把颜色值的R G B 拿到做响应的处理 笔者在这里就不做更多解释  
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
	
	
	//按固定比例缩放
	public static Bitmap scaleDrawableByFixedLen(String filePath){
		return readBitmapAutoSize(filePath);
//		BitmapFactory.Options options = new BitmapFactory.Options();
//	    options.inJustDecodeBounds = true;//不加载图片数据至内存至保存图片的某些参数
//	    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options); //此时返回bm为空
//	    options.inJustDecodeBounds = false;
//	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//	    int be = (int)(options.outHeight / (float)320);
//	    if (be <= 0)
//	        be = 1;
//	    options.inSampleSize = be;
//	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//	    bitmap=BitmapFactory.decodeFile(filePath,options);
////		    int w = bitmap.getWidth();
////		    int h = bitmap.getHeight();
	}
	
	
	//按长宽各自比例进行缩放
	public static Bitmap scaleDrawableByBounds(Bitmap bitmap,int sWidth,int sHeight){
		int bmpWidth  = bitmap.getWidth(); 
	    int bmpHeight  = bitmap.getHeight(); 
	    //缩放图片的尺寸 
	    float scaleWidth  = (float) sWidth / bmpWidth;     //按固定大小缩放  sWidth 写多大就多大
	    float scaleHeight = (float) sHeight / bmpHeight;  //
	    Matrix matrix = new Matrix(); 
	    matrix.postScale(scaleWidth, scaleHeight);//产生缩放后的Bitmap对象 
	    Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false); 
	    bitmap.recycle(); 
	    bitmap = null;
	    System.gc();
	    return resizeBitmap;
	}
	
    // 加水印 也可以加文字
    public static Bitmap watermarkBitmap(Bitmap src, Bitmap watermark,String title) {
        if (src == null) {
            return null;
        }
//        int[] colors = getBitmapColors(src);
        int w = src.getWidth();
        int h = src.getHeight(); 
        //需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了        
        Bitmap newb= Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src  
//        cv.drawBitmap(colors, 0, w, 0, 0, w, h, true, null);
        src.recycle();
        src = null;
        System.gc();
        Paint paint=new Paint();
        //加入图片
        if (watermark != null) {
            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
            paint.setAlpha(50);
            cv.drawBitmap(watermark, w - (ww + 10), h - (wh + 10), paint);// 在src的右下角画入水印            
        }
        //加入文字
        if(title!=null)
        {
            String familyName ="宋体";
            Typeface font = Typeface.create(familyName,Typeface.BOLD);            
            TextPaint textPaint=new TextPaint();
            textPaint.setColor(Color.argb(123, 255,0,0));
            textPaint.setTypeface(font);
            textPaint.setTextSize(35);
            //这里是自动换行的
            StaticLayout layout = new StaticLayout(title,textPaint,w,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
            layout.draw(cv);
            //文字就加左上角算了
            //cv.drawText(title,0,40,paint); 
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        return newb;
    }
    
    /**
     * 1、先判断手机拍照的旋转角度
     * 读取图片属性：旋转的角度
     * @param path
     * 图片绝对路径
     * @return degree旋转的角度
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
    * 2、把生成的照片利用旋转函数旋转过来
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
   public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
     // 旋转图片 动作
	 Bitmap resizedBitmap = null;
	 if(angle != 0){
		 Matrix matrix = new Matrix();
	     matrix.postRotate(angle);
	     // 创建新的图片
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
