package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

/**
 * Represents an object that can be injected as a dependency into another object
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

