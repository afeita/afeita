package com.github.afeita.db.exception;

/**
 * 基异常 
 * @author chensf5
 *
 */
public class AfeiException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AfeiException(){}
	
	public AfeiException(String strMsg){
		super(strMsg);
	}
}
