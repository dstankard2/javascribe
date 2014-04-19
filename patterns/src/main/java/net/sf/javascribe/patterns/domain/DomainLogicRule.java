package net.sf.javascribe.patterns.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

/**
 * 
 * @author DCS
 */
@Scannable
@XmlRootElement(name="domainLogicRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="domainLogicRule",propOrder={ })
public class DomainLogicRule extends ComponentBase implements DomainLogicComponent {

	public int getPriority() { return CorePatternConstants.PRIORITY_DOMAIN_LOGIC_RULE; }
	
	@XmlAttribute
	private String serviceObj = "";
	
	@XmlAttribute
	private String rule = "";
	
	@XmlAttribute
	private String params = "";
	
	@XmlAttribute
	private String returnType = "";

	public String getServiceObj() {
		return serviceObj;
	}

	public void setServiceObj(String serviceObj) {
		this.serviceObj = serviceObj;
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

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}
