package net.sf.javascribe.langsupport.javascript;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;

public class JavascriptFunctionType extends JavascriptVariableType {

	public JavascriptFunctionType(String name) {
		super(name);
	}
	
	@Override
	public JavaScriptCode instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("Javascript function type does not support instantiate");
	}

	@Override
	public JavaScriptCode declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("Javascript function type does not support declare");
	}

}
