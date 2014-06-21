package net.sf.javascribe.api;

/**
 * Represents an exception while generating code.
 * @author DCS
 *
 */
public class JavascribeException extends Exception {
	static final long serialVersionUID = 1L;
	
	public JavascribeException(String message) {
		super(message);
	}

	public JavascribeException(Throwable cause) {
		super(cause);
	}

	public JavascribeException(String message,Throwable cause) {
		super(message,cause);
	}

}

