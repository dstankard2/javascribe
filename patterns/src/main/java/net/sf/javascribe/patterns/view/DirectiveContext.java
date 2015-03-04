package net.sf.javascribe.patterns.view;

import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptFunction;

public interface DirectiveContext {
	
	public ProcessorContext getProcessorContext();
	public CodeExecutionContext getExecCtx();
	public String getElementName();

	public Map<String,String> getDomAttributes();
	public Map<String,String> getTemplateAttributes();
	
	public StringBuilder getCode();
	public String getElementVarName();
	public String getContainerVarName();
	public String newVarName(String baseName,String type,CodeExecutionContext execCtx);
	public void continueRenderElement(CodeExecutionContext execCtx) throws JavascribeException;
	public void continueRenderElement() throws JavascribeException;
	public String getInnerHtml();
	public String getTemplateObj();
	public JavascriptFunction getFunction();

}

