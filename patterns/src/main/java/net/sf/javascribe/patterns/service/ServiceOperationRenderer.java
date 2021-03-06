package net.sf.javascribe.patterns.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.jsom.java5.Java5CodeSnippet;

public interface ServiceOperationRenderer {

	public void setGeneratorContext(ProcessorContext ctx);
	public Java5CodeSnippet getCode(CodeExecutionContext execCtx) throws JavascribeException;

}

