package net.sf.javascribe.patterns.translator;

import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;

public interface FieldTranslatorContext {

	public ProcessorContext getCtx();
	public CodeExecutionContext getExecCtx();
	public Map<String,String> getParams();

}
