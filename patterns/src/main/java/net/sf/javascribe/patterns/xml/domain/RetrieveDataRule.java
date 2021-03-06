package net.sf.javascribe.patterns.xml.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;
import net.sf.javascribe.patterns.domain.DomainLogicComponent;

@Scannable
@XmlRootElement(name="retrieveDataRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="retrieveDataRule",propOrder={ })
public class RetrieveDataRule extends ComponentBase implements DomainLogicComponent {

	public static final String RESOLVE_RULE_STRATEGY = "net.sf.javascribe.patterns.domain.RetrieveDataRule.strategy";

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_RETRIEVE_DATA_RULE; }
	
	@XmlAttribute
	private String strategy = "";
	
	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	@XmlAttribute
	private String serviceObj = "";

	@XmlAttribute
	private String rule = "";
	
	@XmlAttribute
	private String returnAttribute = "";
	
	public String getReturnAttribute() {
		return returnAttribute;
	}

	public void setReturnAttribute(String returnAttribute) {
		this.returnAttribute = returnAttribute;
	}

	@XmlAttribute
	private String params = "";

	@XmlAttribute
	private String dependencies = "";

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

	public String getDependencies() {
		return dependencies;
	}

	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}
}

