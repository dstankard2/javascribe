package net.sf.javascribe.engine;

public class EngineInitException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EngineInitException() {
		super();
	}

	public EngineInitException(String msg) {
		super(msg);
	}

	public EngineInitException(String msg,Throwable cause) {
		super(msg, cause);
	}

}
