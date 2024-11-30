package net.sf.javascribe.patterns.xml.java.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.sf.javascribe.api.annotation.Plugin;
import net.sf.javascribe.api.annotation.XmlConfig;
import net.sf.javascribe.api.config.Component;
import net.sf.javascribe.patterns.PatternPriority;

@XmlConfig
@Plugin
@XmlRootElement(name="domainDataRules")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="domainDataRules",propOrder={  })
public class DomainDataRules extends Component {

	@Override
	public int getPriority() {
		return PatternPriority.DOMAIN_DATA_RULES;
	}
	
	@XmlElement(name = "rules")
	private String content = "";

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
