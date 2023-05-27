package net.sf.javascribe.langsupport.java.types;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.VariableType;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface JavaVariableType extends VariableType {

	/**
	 * Fully qualified class name, or null if the package is "java.lang".
	 * @return
	 */
	public String getImport();

	/**
	 * The class name without the package.
	 * @return
	 */
	public String getClassName();

	public JavaCode declare(String name, CodeExecutionContext execCtx) throws JavascribeException;
	
}
