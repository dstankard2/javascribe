package net.sf.javascribe.langsupport.java;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public class ExceptionType extends JavaVariableTypeBase {
	
	public ExceptionType(String name,String pkg,String className) {
		super(name,pkg,className);
	}
	
	public JavaCode declare(String varName) throws JavascribeException {
		return (JavaCode)declare(varName,null);
	}

	@Override
	public JavaCode instantiate(String name, String value,
			CodeExecutionContext execCtx) throws RuntimeException {
		throw new RuntimeException("Java exceptions do not support instantiate.");
	}

}
