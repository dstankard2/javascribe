package net.sf.javascribe.patterns.translator;

import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;

public class FieldTranslatorContextImpl implements FieldTranslatorContext {
	ProcessorContext ctx = null;
	CodeExecutionContext execCtx = null;
	Map<String,String> params = null;
	public FieldTranslatorContextImpl(ProcessorContext ctx,CodeExecutionContext execCtx,Map<String,String> params) {
		this.params = params;
		this.ctx = ctx;
		this.execCtx = execCtx;
	}
	@Override
	public ProcessorContext getCtx() {
		return ctx;
	}

	@Override
	public CodeExecutionContext getExecCtx() {
		return execCtx;
	}

	@Override
	public Map<String, String> getParams() {
		return params;
	}

}

