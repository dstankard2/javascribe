package net.sf.javascribe.patterns.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.GeneratorContext;
import net.sf.javascribe.api.JavascribeException;
import net.sf.javascribe.api.JavascribeUtils;
import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.langsupport.java.JavaOperation;
import net.sf.javascribe.langsupport.java.JavaServiceObjectType;

@Scannable
@XmlRootElement(name="callBusinessRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="callBusinessRule",propOrder={  })
public class CallBusinessRuleOperation extends ServiceOperation implements ResultServiceOperation {

	public ServiceOperationRenderer getRenderer() {
		return new CallBusinessRuleRenderer(this);
	}

	@XmlAttribute
	private String result = null;
	
	@XmlAttribute
	private String params = null;

	@XmlAttribute
	private String rule = null;

	@Override
	public String getResultType(GeneratorContext ctx) throws JavascribeException {
		String obj = JavascribeUtils.getObjectName(rule);
		String ruleName = JavascribeUtils.getRuleName(rule);
		JavaServiceObjectType type = (JavaServiceObjectType)ctx.getTypes().getType(obj);
		String ret = null;
		if (type==null) {
			throw new JavascribeException("Couldn't find business object '"+obj+"'");
		}
		JavaOperation op = type.getMethod(ruleName);
		if (op==null) throw new JavascribeException("Could not find business rule "+rule);
		ret = op.getReturnType();
		
		return ret;
	}

	@Override
	public String getResultName(GeneratorContext ctx) {
		return result;
	}
	
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
	
}
