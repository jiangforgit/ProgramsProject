
package com.vcom.uploadfile.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
	
	/*
	 * 对文件内容md5
	 */
	public static String getMD5(File file) {  
	     FileInputStream fis = null;  
	     try {  
	         MessageDigest md = MessageDigest.getInstance("MD5");  
	         fis = new FileInputStream(file);  
	         byte[] buffer = new byte[2048];  
	         int length = -1;  
	         long s = System.currentTimeMillis();  
	         while ((length = fis.read(buffer)) != -1) {  
	             md.update(buffer, 0, length);  
	         }  
	        //32位加密  
	        byte[] b = md.digest();  
	        return BytesDealUtil.bytesToHexString(b);  
	        // 16位加密  
	        // return buf.toString().substring(8, 24);  
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	            return null;  
	        }finally {  
	        try {  
	            fis.close();  
	        } catch (IOException ex) {  
	            ex.printStackTrace();  
	        }  
	      }  
	   }  
	  
	/*
	 * 对字符串md5
	 */
	public static String md5Encode(String string) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = string.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
		{
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++)
		{
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
			{
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
