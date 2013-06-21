package net.sf.javascribe.patterns.servlet;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.VariableType;
import net.sf.javascribe.langsupport.java.JavaCode;


public class ServletFilterType implements VariableType {
	private String name = null;
	
	public ServletFilterType(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public JavaCode declare(String varName,CodeExecutionContext execCtx)
			throws JavascribeException {
		return null;
	}

	@Override
	public JavaCode instantiate(String varName, String value,CodeExecutionContext execCtx)
			throws JavascribeException {
		return null;
	}

	
}
