package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * Represents a Java object that can be instantiated and injected as a 
 * dependency into another object.  Especially for use with business services to implement dependency injection.
 * @author DCS
 */
public interface Injectable extends JavaVariableType {

	/**
	 * Returns the code required to declare and instantiate the type in a ready-to-inject state.
	 * @param instanceName
	 * @param execCtx
	 * @return
	 * @throws JavascribeException
	 */
	public JavaCode getInstance(String instanceName,CodeExecutionContext execCtx) throws JavascribeException;

}

