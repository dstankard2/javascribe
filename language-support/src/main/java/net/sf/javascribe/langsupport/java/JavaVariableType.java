package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.VariableType;

/**
 * Represents a type that is a java type.  A java type has a class name 
 * and an import that must be specified in Java import statements.
 * @author DCS
 */
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

	public JavaCode declare(String name, CodeExecutionContext execCtx);
	
}
