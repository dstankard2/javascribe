package net.sf.javascribe.patterns.xml.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public interface ResultOperation {

	public String getResultType(ProcessorContext ctx, CodeExecutionContext execCtx) throws JavascribeException;
	public String getResultName(ProcessorContext ctx, CodeExecutionContext execCtx) throws JavascribeException;

}

