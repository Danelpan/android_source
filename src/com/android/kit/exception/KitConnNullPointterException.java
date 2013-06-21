package com.android.kit.exception;
/**
 * 获取联网时候的初始化Conn空指针异常
 * @author Danel
 *
 */
public class KitConnNullPointterException extends NullPointerException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7438267538264097787L;
	
	public KitConnNullPointterException(){
		super();
	}
	
	public KitConnNullPointterException(String msg){
		super(msg);
	}

}
