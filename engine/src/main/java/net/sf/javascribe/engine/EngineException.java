package net.sf.javascribe.engine;

@SuppressWarnings("serial")
public class EngineException extends RuntimeException {

	public EngineException() { }
	
	public EngineException(String message) {
		super(message);
	}

	public EngineException(Throwable cause) {
		super(cause);
	}

	public EngineException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
