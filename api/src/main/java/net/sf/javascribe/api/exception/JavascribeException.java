package net.sf.javascribe.api.exception;

public class JavascribeException extends Exception {
	private static final long serialVersionUID = 1L;

	public JavascribeException() {
		super();
	}
	
	public JavascribeException(String msg) {
		super(msg);
	}
	
	public JavascribeException(String msg,Throwable cause) {
		super(msg,cause);
	}
	
}
