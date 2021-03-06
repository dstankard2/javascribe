package net.sf.javascribe.patterns.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.patterns.xml.service.InvalidCaseOperation;
import net.sf.jsom.java5.Java5CodeSnippet;

public class InvalidCaseRenderer implements NestingServiceOperationRenderer {
	InvalidCaseOperation op = null;
	ProcessorContext ctx = null;
	
	public void setGeneratorContext(ProcessorContext ctx) {
		this.ctx = ctx;
	}
	
	public InvalidCaseRenderer(InvalidCaseOperation op) {
		this.op = op;
	}
	@Override
	public Java5CodeSnippet getCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append("if (returnValue.getStatus()==1) {\n");
		
		return ret;
	}

	@Override
	public Java5CodeSnippet endingCode(CodeExecutionContext execCtx) throws JavascribeException {
		Java5CodeSnippet ret = new Java5CodeSnippet();
		
		ret.append("}\n");
		
		return ret;
	}

	
}
