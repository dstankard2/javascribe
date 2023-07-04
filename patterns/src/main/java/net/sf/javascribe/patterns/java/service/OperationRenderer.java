package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public abstract class OperationRenderer {
	protected ProcessorContext ctx;
	
	public OperationRenderer(ProcessorContext ctx) {
		this.ctx = ctx;
	}

	public abstract JavaCode getCode(CodeExecutionContext execCtx) throws JavascribeException;

}

