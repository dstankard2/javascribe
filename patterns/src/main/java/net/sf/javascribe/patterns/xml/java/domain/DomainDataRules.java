package net.sf.javascribe.patterns.xml.java.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
