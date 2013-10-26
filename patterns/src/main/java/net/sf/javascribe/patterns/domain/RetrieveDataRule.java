package net.sf.javascribe.patterns.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Scannable;
import net.sf.javascribe.api.config.ComponentBase;
import net.sf.javascribe.patterns.CorePatternConstants;

@Scannable
@XmlRootElement(name="retrieveDataRule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="retrieveDataRule",propOrder={ })
public class RetrieveDataRule extends ComponentBase {

	public static final String DOMAIN_LOGIC_PKG = "net.sf.javascribe.patterns.domain.RetrieveDataRule.pkg";
	public static final String DOMAIN_LOGIC_SERVICE_OBJ = "net.sf.javascribe.patterns.domain.RetrieveDataRule.serviceObj";
	public static final String DOMAIN_LOGIC_LOCATOR_CLASS = "net.sf.javascribe.patterns.domain.RetrieveDataRule.locatorClass";
	public static final String DOMAIN_LOGIC_DEPENDENCIES = "net.sf.javascribe.patterns.domain.RetrieveDataRule.dependencies";

	@Override
	public int getPriority() { return CorePatternConstants.PRIORITY_RETRIEVE_DATA_RULE; }
	
	@XmlAttribute
	private String serviceLocator = "";
	
	@XmlAttribute
	private String serviceObj = "";

	@XmlAttribute
	private String rule = "";
	
	@XmlAttribute
	private String returnType = "";
	
	@XmlAttribute
	private String params = "";

	@XmlAttribute
	private String dependencies = "";

	public String getServiceLocator() {
		return serviceLocator;
	}

	public void setServiceLocator(String serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

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

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
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

