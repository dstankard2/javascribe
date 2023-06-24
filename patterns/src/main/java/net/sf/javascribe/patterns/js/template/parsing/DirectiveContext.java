package net.sf.javascribe.patterns.js.template.parsing;

import java.util.List;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.api.exception.JavascribeException;
import net.sf.javascribe.api.types.ServiceOperation;

public interface DirectiveContext {
	
	ProcessorContext getProcessorContext();
	CodeExecutionContext getExecCtx();
	String getElementName();

	String getDomAttribute(String name);
	String getTemplateAttribute(String name);
	
	StringBuilder getCode();
	String getElementVarName();
	String getContainerVarName();
	String newVarName(String baseName,String type,CodeExecutionContext execCtx);
	void continueRenderElement(CodeExecutionContext execCtx) throws JavascribeException;
	void continueRenderElement() throws JavascribeException;
	String getInnerHtml();
	String getTemplateObj();
	ServiceOperation getFunction();
	List<String> getPreviousEltVars();

	boolean isJavascriptDebug();

	void importModule(String typeName,String jsPath);

}

