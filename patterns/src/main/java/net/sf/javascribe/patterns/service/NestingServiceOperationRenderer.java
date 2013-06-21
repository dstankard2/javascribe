package net.sf.javascribe.patterns.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.jsom.java5.Java5CodeSnippet;

public interface NestingServiceOperationRenderer extends ServiceOperationRenderer {

	public Java5CodeSnippet endingCode(CodeExecutionContext execCtx) throws JavascribeException;

}
