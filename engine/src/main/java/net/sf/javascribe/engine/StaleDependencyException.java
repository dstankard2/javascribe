package net.sf.javascribe.engine;

/**
 * If application data is being modified, and it wasn't added in the current run, 
 * the items that affected it might have to be reset.  This exception should be thrown
 * if that's the case.  This exception is for internal engine use only.
 * @author dstan
 */
@SuppressWarnings("serial")
public class StaleDependencyException extends EngineException {
	private String lang = null;
	private String name = null;
	
	public String getLang() {
		return lang;
	}
	public String getName() {
		return name;
	}

	public StaleDependencyException(String lang, String name) {
		this.lang = lang;
		this.name = name;
	}

	public StaleDependencyException(Throwable cause) {
		super(cause);
	}

	public StaleDependencyException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
