package net.sf.javascribe.patterns.xml.java.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.patterns.java.service.CallRuleRenderer;
import net.sf.javascribe.patterns.java.service.OperationRenderer;

@Plugin
@XmlConfig
@XmlRootElement(name="callRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="callRule",propOrder={ })
public class CallRuleOperation extends Operation {

	@Override 
	public OperationRenderer getRenderer() { return new CallRuleRenderer(this); }

	@XmlAttribute
	private String result = "";
	
	@XmlAttribute
	private String params = "";

	@XmlAttribute
	private String rule = "";
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	/*
	@Override
	public String getResultType(ProcessorContext ctx, CodeExecutionContext execCtx) throws JavascribeException {
		if (result==null) return null;
		List<PropertyEntry> paramEntries = JavascribeUtils.readParametersAsList(params, ctx);
		ServiceOperation op = JavaUtils.findRule(rule,paramEntries,ctx, execCtx);

		return op.getReturnType();
	}

	@Override
	public String getResultName(ProcessorContext ctx, CodeExecutionContext execCtx) {
		if ((result==null) || (result.trim().length()==0)) return null;
		return result;
	}
*/

}

