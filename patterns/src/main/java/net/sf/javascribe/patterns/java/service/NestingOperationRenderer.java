package net.sf.javascribe.patterns.java.service;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.langsupport.java.JavaCode;

public abstract class NestingOperationRenderer extends OperationRenderer {

	public NestingOperationRenderer(ProcessorContext ctx) {
		super(ctx);
	}

	public abstract JavaCode endingCode(CodeExecutionContext execCtx) throws JavascribeException;

}
