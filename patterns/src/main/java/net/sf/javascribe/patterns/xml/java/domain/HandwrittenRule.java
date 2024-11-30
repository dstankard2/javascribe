
package net.sf.javascribe.patterns.xml.java.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="handwrittenRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="handwrittenRule",propOrder={  })
public class HandwrittenRule extends DomainLogicComponent {

	@Override
	public int getPriority() {
		return PatternPriority.HANDWRITTEN_DOMAIN_RULE;
	}

	@XmlAttribute
	private String returnType = "";
	
	@XmlAttribute
	private String rule = "";
	
	@XmlAttribute
	private String params = "";

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public String getComponentName() {
		return this.getServiceName()+'.'+this.getRule()+"("+params+")";
	}

}

