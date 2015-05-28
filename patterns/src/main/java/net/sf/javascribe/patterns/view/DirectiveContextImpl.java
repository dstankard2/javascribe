package net.sf.javascribe.patterns.view;

import java.util.HashMap;
import java.util.Map;

import net.sf.javascribe.api.CodeExecutionContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.ProcessorContext;
import net.sf.javascribe.langsupport.javascript.JavascriptFunctionType;

public class DirectiveContextImpl implements DirectiveContext {
	ProcessorContext ctx = null;
	String elementName = null;
	HashMap<String,String> domAttributes = new HashMap<String,String>();
	HashMap<String,String> templateAttributes = new HashMap<String,String>();
	String containerVar = null;
	String elementVarName = null;
	CodeExecutionContext execCtx = null;
	StringBuilder code = new StringBuilder();
	ElementParser invoker = null;
	String templateObj = null;
	JavascriptFunctionType function = null;
	String innerHtml = null;
	
	public DirectiveContextImpl(ProcessorContext ctx,String elementName,HashMap<String,String> attributes,String containerVar,StringBuilder code,ElementParser invoker,String templateObj,JavascriptFunctionType fn,String innerHtml) {
		this.ctx = ctx;
		this.elementName = elementName;
		
		for(String name : attributes.keySet()) {
			String val = attributes.get(name);
			if (name.startsWith("js-")) templateAttributes.put(name, val);
			else domAttributes.put(name, val);
		}
		this.containerVar = containerVar;
		this.code = code;
		this.invoker = invoker;
		this.templateObj = templateObj;
		this.function = fn;
		this.innerHtml = innerHtml;
	}
	
	@Override
	public Map<String, String> getDomAttributes() {
		return domAttributes;
	}

	@Override
	public Map<String, String> getTemplateAttributes() {
		return templateAttributes;
	}

	@Override
	public ProcessorContext getProcessorContext() {
		return ctx;
	}

	public void setExecCtx(CodeExecutionContext execCtx) {
		this.execCtx = execCtx;
	}
	@Override
	public CodeExecutionContext getExecCtx() {
		return execCtx;
	}

	@Override
	public String getElementName() {
		return elementName;
	}

	@Override
	public StringBuilder getCode() {
		return code;
	}

	public void setElementVarName(String elementVarName) {
		this.elementVarName = elementVarName;
	}
	@Override
	public String getElementVarName() {
		return elementVarName;
	}

	@Override
	public String getContainerVarName() {
		return containerVar;
	}

	@Override
	public String newVarName(String baseName, String type,
			CodeExecutionContext execCtx) {
		return invoker.newVarName(baseName, type, execCtx);
	}

	@Override
	public void continueRenderElement(CodeExecutionContext execCtx)
			throws JavascribeException {
		invoker.continueParsing(execCtx, this);
	}

	public void continueRenderElement() throws JavascribeException {
		continueRenderElement(execCtx);
	}
	
	@Override
	public String getInnerHtml() {
		return innerHtml;
	}

	@Override
	public String getTemplateObj() {
		return templateObj;
	}

	@Override
	public JavascriptFunctionType getFunction() {
		return function;
	}

}

