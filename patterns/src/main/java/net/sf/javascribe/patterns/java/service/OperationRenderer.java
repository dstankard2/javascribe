package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;

public abstract class OperationRenderer {
	protected ProcessorContext ctx;
	
	public abstract void render(RendererContext ctx) throws JavascribeException;
	
	//public abstract JavaCode getCode(CodeExecutionContext execCtx) throws JavascribeException;

}

