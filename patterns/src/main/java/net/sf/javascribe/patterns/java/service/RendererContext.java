package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public interface RendererContext {

	JavaCode getCode();
	CodeExecutionContext execCtx();
	void handleNesting(CodeExecutionContext execCtx) throws JavascribeException;
	void addResultProperty(String name, String type) throws JavascribeException;
	ProcessorContext ctx();
	String getResultVar();

}

