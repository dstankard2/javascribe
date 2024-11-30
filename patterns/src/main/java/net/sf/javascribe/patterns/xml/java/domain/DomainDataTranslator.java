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
@XmlRootElement(name="domainDataTranslator")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="domainDataTranslator",propOrder={  })
public class DomainDataTranslator extends DomainLogicComponent {
	
	@Override
	public int getPriority() {
		return PatternPriority.DOMAIN_DATA_TRANSLATOR;
	}

	@XmlAttribute
	private String returnAttribute = "";
	
	@XmlAttribute
	private String params = "";

	public String getReturnAttribute() {
		return returnAttribute;
	}

	public void setReturnAttribute(String returnAttribute) {
		this.returnAttribute = returnAttribute;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	public String getComponentName() {
		return "DomainDataTranslator["+getReturnAttribute()+"]";
	}

}

