package com.vcom.uploadfile.enums;

public enum EnumContentType {

	//0:�ı�  1��ͼƬ 2������ 3����Ƶ
	Text(2),Voice(3),Photo(4),video(5);
	
	private int type;
	
	private EnumContentType(int Type){
		type = Type;
	}
	
	public int getType(){
		return this.type;
	}
}
