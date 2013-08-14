package com.android.kit.exception;

/**
 * 无可用网络异常，当需要处理该异常的话，只要捕获该异常即可
 * @author Danel
 *
 */
public class NoNetworkException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoNetworkException() {
		super();
	}

	public NoNetworkException(String message) {
		super(message);
	}
	
}
