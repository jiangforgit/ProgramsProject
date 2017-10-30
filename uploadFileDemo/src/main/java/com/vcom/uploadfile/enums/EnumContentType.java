package com.vcom.uploadfile.enums;

public enum EnumContentType {

	//0:Œƒ±æ  1£∫Õº∆¨ 2£∫”Ô“Ù 3£∫ ”∆µ
	Text(2),Voice(3),Photo(4),video(5);
	
	private int type;
	
	private EnumContentType(int Type){
		type = Type;
	}
	
	public int getType(){
		return this.type;
	}
}
