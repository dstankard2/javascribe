package net.sf.javascribe.patterns.view.impl;

import net.sf.javascribe.api.Code;
import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.langsupport.javascript.JavascriptVariableType;

public class TabsetType implements JavascriptVariableType {

	@Override
	public String getName() {
		return "Tabset";
	}
	
	@Override
	public Code instantiate(String name, String value,
			CodeExecutionContext execCtx) throws JavascribeException {
		throw new JavascribeException("Not Supported");
	}

	@Override
	public Code declare(String name, CodeExecutionContext execCtx)
			throws JavascribeException {
		throw new JavascribeException("Not Supported");
	}

}
