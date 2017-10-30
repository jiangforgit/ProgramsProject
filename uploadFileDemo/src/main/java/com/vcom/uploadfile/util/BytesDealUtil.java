package com.vcom.uploadfile.util;

public class BytesDealUtil {

	 /** 
     * 注释：int到字节数组的转换！ 
     * 
     * @param number 
     * @return 
     */ 
    public static byte[] intToByte(int number) { 
    	byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (number >> 8 * (3 - i) & 0xFF);
		}
		return b;
    } 
 
    /** 
     * 注释：字节数组到int的转换！ 
     * 
     * @param b 
     * @return 
     */ 
    public static int byteToInt(byte[] b) { 
    	int intValue = 0;
		for(int i=0;i<b.length;i++)
		{
			intValue += (b[i] & 0xFF)<<(8*(3-i));
		}
		return intValue;
    } 
    
    /* byte[] 转换成 16进制字符串
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。  
    * @param src byte[] data  
    * @return hex string  
    */     
    public static String bytesToHexString(byte[] src){  
       StringBuilder stringBuilder = new StringBuilder("");  
       if (src == null || src.length <= 0) {  
           return null;  
       }  
       for (int i = 0; i < src.length; i++) {  
           int v = src[i] & 0xFF;  
           String hv = Integer.toHexString(v);  
           if (hv.length() < 2) {  
               stringBuilder.append(0);  
           }  
           stringBuilder.append(hv);  
       }  
       return stringBuilder.toString();  
    }  
   
    /** 
     * 字符串转换成 16进制byte[] 
    * Convert hex string to byte[] 
    * @param hexString the hex string 
    * @return byte[] 
    */  
    public static byte[] hexStringToBytes(String hexString) {  
    	if (hexString == null || hexString.equals("")) {
			return null;
		}
		String s = "0123456789ABCDEF";
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (s.indexOf(hexChars[pos]) << 4 | s.indexOf(hexChars[pos + 1]));
		}
		return d;
    }  
    
   public static byte[] longToByte(long number) { 
        long temp = number; 
        byte[] b = new byte[8]; 
        for (int i = 0; i < b.length; i++) { 
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    } 
    
    //byte数组转成long 
    public static long byteToLong(byte[] b) { 
        long s = 0; 
        long s0 = b[0] & 0xff;// 最低位 
        long s1 = b[1] & 0xff; 
        long s2 = b[2] & 0xff; 
        long s3 = b[3] & 0xff; 
        long s4 = b[4] & 0xff;// 最低位 
        long s5 = b[5] & 0xff; 
        long s6 = b[6] & 0xff; 
        long s7 = b[7] & 0xff; 
 
        // s0不变 
        s1 <<= 8; 
        s2 <<= 16; 
        s3 <<= 24; 
        s4 <<= 8 * 4; 
        s5 <<= 8 * 5; 
        s6 <<= 8 * 6; 
        s7 <<= 8 * 7; 
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7; 
        return s; 
    } 
 
    /** 
     * 注释：short到字节数组的转换！ 
     * 
     * @param s 
     * @return 
     */ 
    public static byte[] shortToByte(short number) { 
        int temp = number; 
        byte[] b = new byte[2]; 
        for (int i = 0; i < b.length; i++) { 
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位 
            temp = temp >> 8; // 向右移8位 
        } 
        return b; 
    } 
 
    /** 
     * 注释：字节数组到short的转换！ 
     * 
     * @param b 
     * @return 
     */ 
    public static short byteToShort(byte[] b) { 
        short s = 0; 
        short s0 = (short) (b[0] & 0xff);// 最低位 
        short s1 = (short) (b[1] & 0xff); 
        s1 <<= 8; 
        s = (short) (s0 | s1); 
        return s; 
    }
    
    private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    }  
    
     // subBytes 截取count长度的byte[]
 	 public static byte[] subBytes(byte[] src, int begin, int count) {
 	        byte[] bs = new byte[count];
 	        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
 	        return bs;
 	 }
}
